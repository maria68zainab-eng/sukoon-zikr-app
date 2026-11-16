package com.example

import android.app.Application
import com.example.data.local.SukoonDatabase
import com.example.data.preferences.PreferencesManager
import com.example.data.repository.SukoonRepository
import com.example.notifications.DailyReminderScheduler

class SukoonApplication : Application() {
    lateinit var repository: SukoonRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = SukoonDatabase.getInstance(this)
        val prefs = PreferencesManager(this)
        repository = SukoonRepository(this, database.azkarDao(), prefs)
        DailyReminderScheduler.ensureNotificationChannel(this)
    }
}
