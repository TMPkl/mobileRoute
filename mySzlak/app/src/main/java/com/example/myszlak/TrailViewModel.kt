package com.example.myszlak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TrailViewModel : ViewModel() {

    private val repository = TrailRepository()

    // --- Lista szlaków ---
    private val _trails = MutableStateFlow<List<Trail>>(emptyList())
    val trails: StateFlow<List<Trail>> = _trails

    // --- Pojedynczy szlak (szczegóły) ---
    private val _selectedTrail = MutableStateFlow<Trail?>(null)
    var selectedTrail: StateFlow<Trail?> = _selectedTrail

    // --- Stan ładowania i błędów ---
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // --- Funkcje ---
    fun loadCyclingTrails() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.getCyclingTrails()
                .onSuccess { _trails.value = it }
                .onFailure { _errorMessage.value = it.message }

            _isLoading.value = false
        }
    }

    fun loadWalkingTrails() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.getWalkingTrails()
                .onSuccess { _trails.value = it }
                .onFailure { _errorMessage.value = it.message }

            _isLoading.value = false
        }
    }

    fun loadCyclingTrail(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.getCyclingTrail(id)
                .onSuccess { _selectedTrail.value = it }
                .onFailure { _errorMessage.value = it.message }

            _isLoading.value = false
        }
    }

    fun loadWalkingTrail(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            repository.getWalkingTrail(id)
                .onSuccess { _selectedTrail.value = it }
                .onFailure { _errorMessage.value = it.message }

            _isLoading.value = false
        }
    }
}