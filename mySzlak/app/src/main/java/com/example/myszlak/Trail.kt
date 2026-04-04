package com.example.myszlak

enum class TrailType {
    WALKING,
    CYCLING
}


data class Trail(
    val id: Int,
    val name: String,
    val description: String,
    val length: Double,
    val type: TrailType,
    val imageId: String
)