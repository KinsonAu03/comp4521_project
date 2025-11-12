package com.group22.weatherForecastApp.data.database.dao

import androidx.room.*
import com.group22.weatherForecastApp.data.database.entity.WidgetLayoutEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WidgetLayoutDao {
    @Query("SELECT * FROM widget_layouts WHERE isVisible = 1 ORDER BY position ASC")
    fun getVisibleWidgets(): Flow<List<WidgetLayoutEntity>>

    @Query("SELECT * FROM widget_layouts ORDER BY position ASC")
    fun getAllWidgets(): Flow<List<WidgetLayoutEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidget(widget: WidgetLayoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidgets(widgets: List<WidgetLayoutEntity>)

    @Update
    suspend fun updateWidget(widget: WidgetLayoutEntity)

    @Delete
    suspend fun deleteWidget(widget: WidgetLayoutEntity)

    @Query("UPDATE widget_layouts SET position = :newPosition WHERE id = :id")
    suspend fun updateWidgetPosition(id: Long, newPosition: Int)
}

