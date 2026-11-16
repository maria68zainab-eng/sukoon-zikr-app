package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.security.SecureRandom

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sukoon_prefs")

data class UserPreferences(
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 7,
    val reminderMinute: Int = 0,
    val totalLifetimeBeads: Int = 0,
    val hasAdminPasscode: Boolean = false
)

class PreferencesManager(private val context: Context) {

    private object Keys {
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val TOTAL_LIFETIME_BEADS = intPreferencesKey("total_lifetime_beads")
        val ADMIN_PASSCODE_HASH = stringPreferencesKey("admin_passcode_hash")
        val ADMIN_PASSCODE_SALT = stringPreferencesKey("admin_passcode_salt")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            soundEnabled = prefs[Keys.SOUND_ENABLED] ?: true,
            vibrationEnabled = prefs[Keys.VIBRATION_ENABLED] ?: true,
            reminderEnabled = prefs[Keys.REMINDER_ENABLED] ?: false,
            reminderHour = prefs[Keys.REMINDER_HOUR] ?: 7,
            reminderMinute = prefs[Keys.REMINDER_MINUTE] ?: 0,
            totalLifetimeBeads = prefs[Keys.TOTAL_LIFETIME_BEADS] ?: 0,
            hasAdminPasscode = !prefs[Keys.ADMIN_PASSCODE_HASH].isNullOrBlank()
        )
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.VIBRATION_ENABLED] = enabled }
    }

    suspend fun setReminder(enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.REMINDER_ENABLED] = enabled
            it[Keys.REMINDER_HOUR] = hour
            it[Keys.REMINDER_MINUTE] = minute
        }
    }

    suspend fun incrementLifetimeBeads(delta: Int = 1) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.TOTAL_LIFETIME_BEADS] ?: 0
            prefs[Keys.TOTAL_LIFETIME_BEADS] = (current + delta).coerceAtLeast(0)
        }
    }

    suspend fun setAdminPasscode(pin: String) {
        val saltBytes = ByteArray(16).apply { SecureRandom().nextBytes(this) }
        val saltHex = saltBytes.joinToString("") { "%02x".format(it) }
        val hash = sha256("$saltHex:$pin")
        context.dataStore.edit { prefs ->
            prefs[Keys.ADMIN_PASSCODE_SALT] = saltHex
            prefs[Keys.ADMIN_PASSCODE_HASH] = hash
        }
    }

    suspend fun verifyAdminPasscode(pin: String): Boolean {
        var matched = false
        context.dataStore.edit { prefs ->
            val salt = prefs[Keys.ADMIN_PASSCODE_SALT]
            val storedHash = prefs[Keys.ADMIN_PASSCODE_HASH]
            if (!salt.isNullOrBlank() && !storedHash.isNullOrBlank()) {
                matched = sha256("$salt:$pin") == storedHash
            }
        }
        return matched
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}
