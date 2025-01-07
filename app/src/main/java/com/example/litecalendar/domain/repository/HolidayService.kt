package com.example.litecalendar.domain.repository

import com.example.litecalendar.data.local.HolidayEntity
import com.example.litecalendar.utils.Resource
import kotlinx.coroutines.flow.Flow

interface HolidayService {
    suspend fun getHolidays(country : String, year : Int, fetchFromRemote : Boolean) : Flow<Resource<List<HolidayEntity>>>
    suspend fun getHolidaysQuery(query: String, country : String, year : Int, fetchFromRemote : Boolean) : Flow<Resource<List<HolidayEntity>>>
}