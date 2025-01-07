package com.example.litecalendar.data.mapper

import com.example.litecalendar.data.local.HolidayEntity
import com.example.litecalendar.data.remote.dto.Country
import com.example.litecalendar.data.remote.dto.Date
import com.example.litecalendar.data.remote.dto.Datetime
import com.example.litecalendar.data.remote.dto.HolidayDto
import com.example.litecalendar.domain.model.Holiday

fun HolidayDto.toEntity(): HolidayEntity {
    return HolidayEntity(
        countryId = this.country.id,
        countryName = this.country.name,
        day = this.date.datetime.day,
        month = this.date.datetime.month,
        year = this.date.datetime.year,
        isoDate = this.date.iso,
        description = this.description,
        locations = this.locations,
        name = this.name,
        primaryType = this.primaryType,
        states = this.states,
        type = this.type.joinToString(",") // Convert list to comma-separated string
    )
}

fun HolidayEntity.toDto(): HolidayDto {
    return HolidayDto(
        country = Country(id = this.countryId, name = this.countryName),
        date = Date(
            datetime = Datetime(day = this.day, month = this.month, year = this.year),
            iso = this.isoDate
        ),
        description = this.description,
        locations = this.locations,
        name = this.name,
        primaryType = this.primaryType,
        states = this.states,
        type = this.type.split(",") // Convert comma-separated string back to list
    )
}

fun HolidayEntity.toHoliday(): Holiday {
    return Holiday(
        country = Country(
            id = this.countryId,
            name = this.countryName
        ),
        date = Date(
            datetime = Datetime(
                day = this.day,
                month = this.month,
                year = this.year
            ),
            iso = this.isoDate
        ),
        description = this.description,
        locations = this.locations,
        name = this.name,
        primaryType = this.primaryType,
        states = this.states,
        type = this.type.split(",") // Convert comma-separated string back to a list
    )
}

