package io.github.mamedovilkin.weather.data.mapper

import io.github.mamedovilkin.weather.data.model.LocationDto
import io.github.mamedovilkin.weather.domain.model.Location

fun LocationDto.toDomainLocation() =
    Location(
        latitude = latitude,
        longitude = longitude,
        name = name,
        country = country,
    )