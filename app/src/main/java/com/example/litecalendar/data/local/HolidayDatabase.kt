package com.example.litecalendar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(
    entities = [HolidayEntity::class],
    version = 1,
    exportSchema = false
)
abstract class HolidayDatabase : RoomDatabase() {
    abstract val dao : HolidayDao
}