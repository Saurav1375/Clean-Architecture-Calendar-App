package com.example.litecalendar.data.remote

import com.example.litecalendar.data.remote.dto.HolidayDto
import com.example.litecalendar.data.remote.dto.HolidayResponse
import com.example.litecalendar.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HolidayServiceApi {

    @GET("holidays")
    suspend fun getHolidays(
        @Query("api_key") key : String = Constants.API_KEY,
        @Query("country") country : String,
        @Query("year") year : Int
    ) : HolidayResponse
}