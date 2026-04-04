package com.example.myszlak

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("cycling")
    suspend fun getCyclingTrails(): List<Trail>

    @GET("cycling/{id}")
    suspend fun getCyclingTrail(@Path("id") id: Int): Trail

    @GET("walking")
    suspend fun getWalkingTrails(): List<Trail>

    @GET("walking/{id}")
    suspend fun getWalkingTrail(@Path("id") id: Int): Trail
}