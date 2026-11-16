package com.example.feedback

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class TasbeehFeedbackManager(private val context: Context) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val mgr = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            mgr?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun playBeadTap(soundEnabled: Boolean, vibrationEnabled: Boolean) {
        if (vibrationEnabled) {
            vibrateTick(18L, 80)
        }
        if (soundEnabled) {
            scope.launch {
                playSoftTone(frequencyHz = 528.0, durationMs = 55, amplitude = 0.22f)
            }
        }
    }

    fun playStageMilestone(soundEnabled: Boolean, vibrationEnabled: Boolean) {
        if (vibrationEnabled) {
            vibratePattern(longArrayOf(0, 22, 60, 30), intArrayOf(0, 110, 0, 150))
        }
        if (soundEnabled) {
            scope.launch {
                playSoftChord(frequencies = listOf(440.0, 554.37, 659.25), durationMs = 260, amplitude = 0.16f)
            }
        }
    }

    fun playCompletion(soundEnabled: Boolean, vibrationEnabled: Boolean) {
        if (vibrationEnabled) {
            vibratePattern(longArrayOf(0, 35, 80, 45, 90, 65), intArrayOf(0, 140, 0, 170, 0, 210))
        }
        if (soundEnabled) {
            scope.launch {
                playSoftChord(frequencies = listOf(432.0, 540.0, 648.0, 864.0), durationMs = 520, amplitude = 0.18f)
            }
        }
    }

    private fun vibrateTick(durationMs: Long, amplitude: Int) {
        try {
            val v = vibrator ?: return
            if (!v.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(durationMs)
            }
        } catch (_: Exception) {
        }
    }

    private fun vibratePattern(timings: LongArray, amplitudes: IntArray) {
        try {
            val v = vibrator ?: return
            if (!v.hasVibrator()) return
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(timings, -1)
            }
        } catch (_: Exception) {
        }
    }

    private fun playSoftTone(frequencyHz: Double, durationMs: Int, amplitude: Float) {
        playSoftChord(listOf(frequencyHz), durationMs, amplitude)
    }

    private fun playSoftChord(frequencies: List<Double>, durationMs: Int, amplitude: Float) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * durationMs) / 1000
            if (numSamples <= 0) return

            val pcm = ShortArray(numSamples)
            val attackSamples = (sampleRate * 0.006).toInt().coerceAtLeast(1)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val attack = if (i < attackSamples) i.toDouble() / attackSamples else 1.0
                val decay = exp(-5.2 * i.toDouble() / numSamples)
                var sampleSum = 0.0
                for (freq in frequencies) {
                    sampleSum += sin(2.0 * PI * freq * t)
                }
                val normalized = (sampleSum / frequencies.size) * attack * decay * amplitude
                pcm[i] = (normalized.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
            }

            val minBuffer = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferBytes = maxOf(minBuffer, pcm.size * 2)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferBytes)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(pcm, 0, pcm.size)
            track.play()
            Thread.sleep(durationMs.toLong() + 30L)
            track.stop()
            track.release()
        } catch (_: Exception) {
        }
    }
}
