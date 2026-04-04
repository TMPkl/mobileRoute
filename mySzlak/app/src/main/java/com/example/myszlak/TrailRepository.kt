package com.example.myszlak

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrailRepository {

    private val api = RetrofitClient.apiService

    suspend fun getCyclingTrails(): Result<List<Trail>> = withContext(Dispatchers.IO) {
        runCatching { api.getCyclingTrails() }
    }

    suspend fun getCyclingTrail(id: Int): Result<Trail> = withContext(Dispatchers.IO) {
        runCatching { api.getCyclingTrail(id) }
    }

    suspend fun getWalkingTrails(): Result<List<Trail>> = withContext(Dispatchers.IO) {
        runCatching { api.getWalkingTrails() }
    }

    suspend fun getWalkingTrail(id: Int): Result<Trail> = withContext(Dispatchers.IO) {
        runCatching { api.getWalkingTrail(id) }
    }
}