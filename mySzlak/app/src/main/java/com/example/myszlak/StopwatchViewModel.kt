package com.example.myszlak

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StopwatchViewModel(application: Application) : AndroidViewModel(application) {

    private var service: StopwatchService? = null
    private var bound = false

    private val _elapsedSeconds = MutableStateFlow(0L)
    val elapsedSeconds: StateFlow<Long> = _elapsedSeconds

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _currentTrailId = MutableStateFlow<Int?>(null)
    val currentTrailId: StateFlow<Int?> = _currentTrailId

    private val _currentTrailName = MutableStateFlow<String?>(null)
    val currentTrailName: StateFlow<String?> = _currentTrailName

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val b = binder as StopwatchService.StopwatchBinder
            service = b.getService()
            bound = true

            // Obserwujemy stan serwisu
            viewModelScope.launch {
                service?.elapsedSeconds?.collect { _elapsedSeconds.value = it }
            }
            viewModelScope.launch {
                service?.isRunning?.collect { _isRunning.value = it }
            }
            viewModelScope.launch {
                service?.currentTrailId?.collect { _currentTrailId.value = it }
            }
            viewModelScope.launch {
                service?.currentTrailName?.collect { _currentTrailName.value = it }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
            service = null
        }
    }

    fun bindService(context: Context) {
        if (bound) return
        val intent = Intent(context, StopwatchService::class.java)
        context.startService(intent)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        if (bound) {
            context.unbindService(connection)
            bound = false
        }
    }

    fun start(trailId: Int, trailName: String) = service?.start(trailId, trailName)
    fun pause() = service?.pause()
    fun reset() = service?.reset()

    override fun onCleared() {
        super.onCleared()
    }
}
