package com.example.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AzkarEntity
import com.example.data.local.ZikrSessionEntity
import com.example.data.preferences.UserPreferences
import com.example.data.repository.SukoonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: SukoonRepository
) : ViewModel() {

    val dailyZikr: StateFlow<AzkarEntity?> = repository.dailyZikr
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val publishedAzkar: StateFlow<List<AzkarEntity>> = repository.publishedAzkar
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAzkar: StateFlow<List<AzkarEntity>> = repository.allAzkar
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedSessions: StateFlow<List<ZikrSessionEntity>> = repository.completedSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedTargetCount: StateFlow<Int> = repository.completedTargetCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val userPreferences: StateFlow<UserPreferences> = repository.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    private val _isAdminUnlocked = MutableStateFlow(false)
    val isAdminUnlocked: StateFlow<Boolean> = _isAdminUnlocked.asStateFlow()

    private val _adminAuthError = MutableStateFlow<String?>(null)
    val adminAuthError: StateFlow<String?> = _adminAuthError.asStateFlow()

    fun toggleSound() {
        viewModelScope.launch {
            repository.setSoundEnabled(!userPreferences.value.soundEnabled)
        }
    }

    fun toggleVibration() {
        viewModelScope.launch {
            repository.setVibrationEnabled(!userPreferences.value.vibrationEnabled)
        }
    }

    fun updateReminder(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            repository.setReminder(enabled, hour, minute)
        }
    }

    fun setupAdminPasscode(pin: String) {
        if (pin.length < 4) {
            _adminAuthError.value = "Please enter at least 4 digits for your private PIN."
            return
        }
        viewModelScope.launch {
            repository.setAdminPasscode(pin)
            _adminAuthError.value = null
            _isAdminUnlocked.value = true
        }
    }

    fun verifyAdminPasscode(pin: String) {
        viewModelScope.launch {
            val ok = repository.verifyAdminPasscode(pin)
            if (ok) {
                _adminAuthError.value = null
                _isAdminUnlocked.value = true
            } else {
                _adminAuthError.value = "Incorrect PIN. Please try again."
            }
        }
    }

    fun lockAdmin() {
        _isAdminUnlocked.value = false
        _adminAuthError.value = null
    }

    fun clearAdminError() {
        _adminAuthError.value = null
    }

    fun saveZikr(zikr: AzkarEntity, onSaved: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val savedId = repository.upsertZikr(zikr)
            onSaved(savedId)
        }
    }

    fun setDailyZikr(zikrId: Long) {
        viewModelScope.launch {
            repository.setDailyZikr(zikrId)
        }
    }

    fun togglePublished(zikr: AzkarEntity) {
        viewModelScope.launch {
            repository.setPublished(zikr.id, !zikr.isPublished)
        }
    }

    fun deleteZikr(zikrId: Long) {
        viewModelScope.launch {
            repository.deleteZikr(zikrId)
        }
    }

    suspend fun importSceneImage(uri: Uri): String? {
        return repository.copyImageToInternalStorage(uri)
    }

    class Factory(private val repository: SukoonRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }
    }
}
