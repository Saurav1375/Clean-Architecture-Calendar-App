package com.example.litecalendar.data.remote.dto

import com.squareup.moshi.Json

data class HolidayResponse(
    val response: HolidayData
)

data class HolidayData(
    val holidays: List<HolidayDto>
)
data class HolidayDto(
    val country: Country,
    val date: Date,
    val description: String,
    val locations: String,
    val name: String,
    @field:Json(name ="primary_type") val primaryType: String,
    val states: String,
    val type: List<String>
)