package com.example.myszlak

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_trails")
    fun getAllFavorites(): Flow<List<FavoriteTrail>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_trails WHERE id = :id)")
    fun isFavorite(id: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(trail: FavoriteTrail)

    @Delete
    suspend fun deleteFavorite(trail: FavoriteTrail)
}
