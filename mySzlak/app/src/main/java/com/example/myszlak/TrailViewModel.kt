package com.example.myszlak

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TrailRepository()
    private val db = AppDatabase.getDatabase(application)
    private val favoriteDao = db.favoriteDao()

    // --- Lista szlaków ---
    private val _trails = MutableStateFlow<List<Trail>>(emptyList())
    val trails: StateFlow<List<Trail>> = _trails

    // --- Pojedynczy szlak (szczegóły) ---
    private val _selectedTrail = MutableStateFlow<Trail?>(null)
    val selectedTrail: StateFlow<Trail?> = _selectedTrail

    // --- Ulubione ---
    val favoriteTrails: StateFlow<List<Trail>> = favoriteDao.getAllFavorites()
        .flatMapLatest { favorites ->
            kotlinx.coroutines.flow.flowOf(favorites.map { it.toTrail() })
        }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun isFavorite(trailId: Int): StateFlow<Boolean> = favoriteDao.isFavorite(trailId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun toggleFavorite(trail: Trail) {
        viewModelScope.launch {
            val isFav = favoriteDao.isFavorite(trail.id).first()
            if (isFav) {
                favoriteDao.deleteFavorite(trail.toFavorite())
            } else {
                favoriteDao.insertFavorite(trail.toFavorite())
            }
        }
    }

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
    
    fun loadFavorites() {
        _trails.value = favoriteTrails.value
    }
}
