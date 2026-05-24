package io.github.mamedovilkin.weather.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS forecast")
        db.execSQL("DROP TABLE IF EXISTS weather")

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS weather (
                id INTEGER NOT NULL,
                name TEXT,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL,
                temperature REAL NOT NULL,
                feelsLike REAL NOT NULL,
                weatherCode INTEGER NOT NULL,
                wind REAL NOT NULL,
                humidity INTEGER NOT NULL,
                pressure REAL NOT NULL,
                hourly TEXT NOT NULL,
                daily TEXT NOT NULL,
                PRIMARY KEY(id)
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE weather ADD COLUMN isDay INTEGER NOT NULL DEFAULT 1")

        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS locations (
                    id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    lat REAL NOT NULL,
                    lon REAL NOT NULL,
                    PRIMARY KEY(id)
                )
            """.trimIndent()
        )

        db.execSQL(
            """
                CREATE UNIQUE INDEX index_locations_lat_lon
                ON locations(lat, lon)
            """.trimIndent()
        )
    }
}