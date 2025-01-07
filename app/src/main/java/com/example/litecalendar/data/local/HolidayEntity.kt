package com.example.litecalendar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holiday_table")
data class HolidayEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Auto-generated primary key
    val countryId: String,
    val countryName: String,
    val day: Int,
    val month: Int,
    val year: Int,
    val isoDate: String,
    val description: String,
    val locations: String,
    val name: String,
    val primaryType: String,
    val states: String,
    val type: String // Store the list as a comma-separated string
)