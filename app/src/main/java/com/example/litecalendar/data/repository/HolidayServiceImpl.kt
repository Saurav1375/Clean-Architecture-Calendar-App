package com.example.litecalendar.data.repository

import android.util.Log
import com.example.litecalendar.data.local.HolidayDatabase
import com.example.litecalendar.data.local.HolidayEntity
import com.example.litecalendar.data.mapper.toEntity
import com.example.litecalendar.data.remote.HolidayServiceApi
import com.example.litecalendar.domain.repository.HolidayService
import com.example.litecalendar.utils.Constants
import com.example.litecalendar.utils.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class HolidayServiceImpl @Inject constructor(
    private val api: HolidayServiceApi,
    private val localDb: HolidayDatabase
) : HolidayService {
    private val dao = localDb.dao
    override suspend fun getHolidays(
        country: String,
        year: Int,
        fetchFromRemote: Boolean
    ): Flow<Resource<List<HolidayEntity>>> = flow {
        emit(Resource.Loading(isLoading = true))
        val localHolidays = dao.getHolidays()

        if (localHolidays.isNotEmpty()) {
            emit(Resource.Success(localHolidays))
            return@flow
        }
    }

    override suspend fun getHolidaysQuery(
        query: String,
        country: String,
        year: Int,
        fetchFromRemote: Boolean
    ): Flow<Resource<List<HolidayEntity>>> = flow {
        emit(Resource.Loading(isLoading = true))

        // First, try to get filtered results from local database
        val localHolidays = dao.getHolidaysQuery(query)
        emit(Resource.Success(localHolidays))

        // Determine if we need to fetch from remote
        val shouldFetchRemote = (localHolidays.isEmpty() && query.isBlank()) || fetchFromRemote

        if (shouldFetchRemote) {
            try {
                val holidays = fetchHolidaysForThreeYears(country, year)

                dao.clearHolidays()
                dao.insertHolidayData(holidays.map { it.toEntity() })

                // Emit new filtered results
                emit(Resource.Success(dao.getHolidaysQuery(query)))
                Log.d(Constants.TAG, "Holidays updated: ${dao.getHolidaysQuery(query).size} matches found")
            } catch (e: Exception) {
                Log.e(Constants.TAG, "Error fetching holidays", e)
                emit(Resource.Error(
                    message = when (e) {
                        is IOException -> "Network error. Please check your internet connection."
                        is HttpException -> "Server error. Please try again later."
                        else -> "An unexpected error occurred."
                    }
                ))
            }
        }

        emit(Resource.Loading(false))
    }

    private suspend fun fetchHolidaysForThreeYears(country: String, year: Int) = coroutineScope {
        // Fetch all three years concurrently
        val prePrevYear = async { api.getHolidays(country = country, year =year - 2) }
        val prevYear = async { api.getHolidays(country = country, year =year - 1) }
        val currentYear = async { api.getHolidays(country = country, year = year) }
        val nextYear = async { api.getHolidays(country = country, year = year + 1) }
        val nextNextYear = async { api.getHolidays(country = country, year = year + 2) }

        // Combine results
        val allHolidays = listOf(
            prePrevYear.await().response.holidays,
            prevYear.await().response.holidays,
            currentYear.await().response.holidays,
            nextYear.await().response.holidays,
            nextNextYear.await().response.holidays
        ).flatten()

        allHolidays
    }
}