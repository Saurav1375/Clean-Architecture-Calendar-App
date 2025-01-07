package com.example.litecalendar.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HolidayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolidayData(holiday : List<HolidayEntity>)

    @Query("SELECT * FROM holiday_table")
    suspend fun getHolidays() : List<HolidayEntity>

    @Query("DELETE FROM holiday_table")
    suspend fun clearHolidays()

    @Query(
        """
            SELECT * FROM holiday_table
            WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%'
        """
    )
    suspend fun getHolidaysQuery(query : String) : List<HolidayEntity>
}