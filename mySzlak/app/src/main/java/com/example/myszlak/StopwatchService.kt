package com.example.myszlak

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StopwatchService : Service() {

    private val binder = StopwatchBinder()

    // Stan stopera
    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _currentTrailId = MutableStateFlow<Int?>(null)
    val currentTrailId: StateFlow<Int?> = _currentTrailId

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    inner class StopwatchBinder : Binder() {
        fun getService(): StopwatchService = this@StopwatchService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(1, createNotification())
        return START_STICKY
    }

    fun start(trailId: Int) {
        if (_currentTrailId.value != trailId) {
            reset()
            _currentTrailId.value = trailId
        }
        
        if (_isRunning.value) return
        
        _isRunning.value = true
        job = scope.launch {
            while (_isRunning.value) {
                delay(1000)
                _elapsedSeconds.value++
            }
        }
    }

    fun pause() {
        _isRunning.value = false
        job?.cancel()
    }

    fun reset() {
        pause()
        _elapsedSeconds.value = 0L
        _currentTrailId.value = null
    }

    fun stopStopwatch() {
        reset()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(): Notification {
        val channelId = "stopwatch_channel"
        val manager = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(channelId, "Stoper", NotificationManager.IMPORTANCE_LOW)
        manager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Stoper działa")
            .setContentText("Trwa mierzenie czasu")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()
    }
}