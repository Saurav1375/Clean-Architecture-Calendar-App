package com.example.litecalendar.utils

import com.example.litecalendar.BuildConfig

object Constants {
    const val TAG = "CALENDERLITE"
    const val PAGE_COUNT = 1800
    const val INITIAL_PAGE = PAGE_COUNT / 2
    const val API_KEY = BuildConfig.API_KEY
    const val WEB_CLIENT_ID = BuildConfig.WEB_CLIENT_ID
    const val DATABASE_NAME = "holiday.db"
    const val BASE_URL = "https://calendarific.com/api/v2/"
    const val CHANNEL_ID = "calender_channel"
    const val CHANNEL_NAME = "event_channel"
}