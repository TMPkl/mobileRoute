package com.example.myszlak

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_trails")
data class FavoriteTrail(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val length: Double,
    val type: String, // Store as String for simplicity in DB
    val imageId: String
)

fun Trail.toFavorite(): FavoriteTrail {
    return FavoriteTrail(
        id = this.id,
        name = this.name,
        description = this.description,
        length = this.length,
        type = this.type.name,
        imageId = this.imageId
    )
}

fun FavoriteTrail.toTrail(): Trail {
    return Trail(
        id = this.id,
        name = this.name,
        description = this.description,
        length = this.length,
        type = TrailType.valueOf(this.type),
        imageId = this.imageId
    )
}
