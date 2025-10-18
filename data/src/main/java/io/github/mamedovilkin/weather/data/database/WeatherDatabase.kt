package io.github.mamedovilkin.weather.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.mamedovilkin.weather.data.dao.WeatherDao
import io.github.mamedovilkin.weather.data.entity.ForecastEntity
import io.github.mamedovilkin.weather.data.entity.WeatherEntity

@Database(entities = [WeatherEntity::class, ForecastEntity::class], version = 1, exportSchema = false)
@TypeConverters(ListConverter::class)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    WeatherDatabase::class.java,
                    "Weather"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}