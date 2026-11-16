package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AzkarEntity
import com.example.data.preferences.UserPreferences
import com.example.data.repository.SukoonRepository
import com.example.domain.SceneThemeRegistry
import com.example.domain.StageProgress
import com.example.feedback.TasbeehFeedbackManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TasbeehUiState(
    val zikr: AzkarEntity? = null,
    val currentCount: Int = 0,
    val targetCount: Int = 100,
    val stageProgress: StageProgress? = null,
    val beadPulseCount: Int = 0,
    val isPaused: Boolean = false,
    val elapsedSeconds: Long = 0L,
    val showCompletionModal: Boolean = false,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val milestoneBanner: String? = null
)

class TasbeehViewModel(
    private val zikrId: Long,
    private val repository: SukoonRepository,
    private val feedbackManager: TasbeehFeedbackManager
) : ViewModel() {

    private val _optimisticCount = MutableStateFlow<Int?>(null)
    private val _beadPulseCount = MutableStateFlow(0)
    private val _isPaused = MutableStateFlow(false)
    private val _elapsedSeconds = MutableStateFlow(0L)
    private val _showCompletionModal = MutableStateFlow(false)
    private val _milestoneBanner = MutableStateFlow<String?>(null)

    private var sessionBeadsIncremented = 0
    private var hasRecordedCurrentCompletion = false

    val uiState: StateFlow<TasbeehUiState> = combine(
        repository.observeZikrById(zikrId),
        repository.userPreferences,
        _optimisticCount,
        _beadPulseCount,
        _isPaused,
        _elapsedSeconds,
        _showCompletionModal,
        _milestoneBanner
    ) { values ->
        val zikr = values[0] as AzkarEntity?
        val prefs = values[1] as UserPreferences
        val optimistic = values[2] as Int?
        val pulses = values[3] as Int
        val paused = values[4] as Boolean
        val elapsed = values[5] as Long
        val showModal = values[6] as Boolean
        val banner = values[7] as String?

        val count = optimistic ?: (zikr?.currentCount ?: 0)
        val target = (zikr?.targetCount ?: 100).coerceAtLeast(1)
        val stage = zikr?.let {
            SceneThemeRegistry.calculateStageProgress(count, target, it.themeId)
        }

        TasbeehUiState(
            zikr = zikr,
            currentCount = count,
            targetCount = target,
            stageProgress = stage,
            beadPulseCount = pulses,
            isPaused = paused,
            elapsedSeconds = elapsed,
            showCompletionModal = showModal,
            soundEnabled = prefs.soundEnabled,
            vibrationEnabled = prefs.vibrationEnabled,
            milestoneBanner = banner
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TasbeehUiState()
    )

    init {
        viewModelScope.launch {
            while (true) {
                delay(1000L)
                val state = uiState.value
                if (state.zikr != null && !state.isPaused && !state.showCompletionModal && state.currentCount < state.targetCount) {
                    _elapsedSeconds.value += 1L
                }
            }
        }
    }

    fun onBeadTapped() {
        val state = uiState.value
        val zikr = state.zikr ?: return
        if (state.isPaused) return

        val current = _optimisticCount.value ?: zikr.currentCount
        val target = zikr.targetCount.coerceAtLeast(1)
        if (current >= target) {
            _showCompletionModal.value = true
            return
        }

        val nextCount = current + 1
        _optimisticCount.value = nextCount
        sessionBeadsIncremented += 1
        _beadPulseCount.value += 1

        val prevStage = SceneThemeRegistry.calculateStageProgress(current, target, zikr.themeId).stage
        val nextStageInfo = SceneThemeRegistry.calculateStageProgress(nextCount, target, zikr.themeId)

        if (nextCount >= target) {
            feedbackManager.playCompletion(state.soundEnabled, state.vibrationEnabled)
            _showCompletionModal.value = true
            if (!hasRecordedCurrentCompletion) {
                hasRecordedCurrentCompletion = true
                viewModelScope.launch {
                    repository.saveBeadProgress(zikr.id, nextCount, incrementLifetime = true)
                    repository.recordSession(
                        zikr = zikr,
                        completedCount = nextCount,
                        durationSeconds = _elapsedSeconds.value,
                        isTargetCompleted = true
                    )
                }
                return
            }
        } else if (nextStageInfo.stage > prevStage) {
            feedbackManager.playStageMilestone(state.soundEnabled, state.vibrationEnabled)
            showMilestone("Stage ${nextStageInfo.stage} · ${nextStageInfo.stageDescription}")
        } else {
            feedbackManager.playBeadTap(state.soundEnabled, state.vibrationEnabled)
        }

        viewModelScope.launch {
            repository.saveBeadProgress(zikr.id, nextCount, incrementLifetime = true)
        }
    }

    private fun showMilestone(text: String) {
        viewModelScope.launch {
            _milestoneBanner.value = text
            delay(2600L)
            if (_milestoneBanner.value == text) {
                _milestoneBanner.value = null
            }
        }
    }

    fun togglePause() {
        _isPaused.value = !_isPaused.value
    }

    fun toggleSound() {
        val next = !uiState.value.soundEnabled
        viewModelScope.launch { repository.setSoundEnabled(next) }
    }

    fun toggleVibration() {
        val next = !uiState.value.vibrationEnabled
        viewModelScope.launch { repository.setVibrationEnabled(next) }
    }

    fun resetProgress() {
        val zikr = uiState.value.zikr ?: return
        val previousCount = _optimisticCount.value ?: zikr.currentCount
        _optimisticCount.value = 0
        viewModelScope.launch {
            if (previousCount > 0 && !hasRecordedCurrentCompletion && sessionBeadsIncremented > 0) {
                repository.recordSession(
                    zikr = zikr,
                    completedCount = previousCount,
                    durationSeconds = _elapsedSeconds.value,
                    isTargetCompleted = false
                )
            }
            sessionBeadsIncremented = 0
            hasRecordedCurrentCompletion = false
            _elapsedSeconds.value = 0L
            _isPaused.value = false
            _showCompletionModal.value = false
            repository.saveBeadProgress(zikr.id, 0, incrementLifetime = false)
        }
    }

    fun dismissCompletionAndRestart() {
        hasRecordedCurrentCompletion = false
        sessionBeadsIncremented = 0
        _elapsedSeconds.value = 0L
        _showCompletionModal.value = false
        _optimisticCount.value = 0
        val zikr = uiState.value.zikr ?: return
        viewModelScope.launch {
            repository.saveBeadProgress(zikr.id, 0, incrementLifetime = false)
        }
    }

    fun dismissCompletionModal() {
        _showCompletionModal.value = false
    }

    class Factory(
        private val zikrId: Long,
        private val repository: SukoonRepository,
        private val feedbackManager: TasbeehFeedbackManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TasbeehViewModel(zikrId, repository, feedbackManager) as T
        }
    }
}
