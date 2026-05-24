package io.github.mamedovilkin.weather.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.mamedovilkin.weather.domain.dao.WeatherDao
import io.github.mamedovilkin.weather.domain.entity.LocationEntity
import io.github.mamedovilkin.weather.domain.entity.WeatherEntity

@Database(entities = [WeatherEntity::class, LocationEntity::class], version = 3, exportSchema = false)
@TypeConverters(WeatherConverters::class)
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
                ).addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}