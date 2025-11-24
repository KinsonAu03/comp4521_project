package com.group22.weatherForecastApp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.group22.weatherForecastApp.data.database.dao.LocationDao
import com.group22.weatherForecastApp.data.database.dao.WeatherDataDao
import com.group22.weatherForecastApp.data.database.dao.WidgetLayoutDao
import com.group22.weatherForecastApp.data.database.entity.LocationEntity
import com.group22.weatherForecastApp.data.database.entity.WeatherDataEntity
import com.group22.weatherForecastApp.data.database.entity.WidgetLayoutEntity

@Database(
    entities = [
        LocationEntity::class,
        WeatherDataEntity::class,
        WidgetLayoutEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun weatherDataDao(): WeatherDataDao
    abstract fun widgetLayoutDao(): WidgetLayoutDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                )
                    .fallbackToDestructiveMigration() // For development - remove in production and add proper migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

