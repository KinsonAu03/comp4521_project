package com.group22.weatherForecastApp.data.database.dao

import androidx.room.*
import com.group22.weatherForecastApp.data.database.entity.LocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations ORDER BY isUsing DESC, isFavorite DESC, `order` ASC, name ASC")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM locations WHERE isUsing = 1 LIMIT 1")
    fun getUsingLocation(): Flow<LocationEntity?>

    @Query("SELECT * FROM locations WHERE isUsing = 0 ORDER BY `order` ASC, name ASC")
    fun getFavoriteLocations(): Flow<List<LocationEntity>>

    @Query("SELECT COUNT(*) FROM locations WHERE isUsing = 0")
    suspend fun getFavoriteLocationsCount(): Int

    @Query("SELECT * FROM locations WHERE isCurrentLocation = 1 LIMIT 1")
    fun getCurrentLocation(): Flow<LocationEntity?>

    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getLocationById(id: Long): LocationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<LocationEntity>)

    @Update
    suspend fun updateLocation(location: LocationEntity)

    @Delete
    suspend fun deleteLocation(location: LocationEntity)

    @Query("DELETE FROM locations WHERE id = :id")
    suspend fun deleteLocationById(id: Long)

    @Query("UPDATE locations SET isCurrentLocation = 0")
    suspend fun clearCurrentLocation()

    @Query("UPDATE locations SET isCurrentLocation = 1 WHERE id = :id")
    suspend fun setCurrentLocation(id: Long)

    @Query("UPDATE locations SET isUsing = 0")
    suspend fun clearUsingLocation()

    @Query("UPDATE locations SET isUsing = 1 WHERE id = :id")
    suspend fun setUsingLocation(id: Long)
}

