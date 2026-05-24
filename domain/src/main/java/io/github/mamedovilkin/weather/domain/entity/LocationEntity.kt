package io.github.mamedovilkin.weather.domain.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "locations",
    indices = [
        Index(
            value = ["lat", "lon"],
            unique = true
        )
    ]
)
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val lat: Double,
    val lon: Double,
)
