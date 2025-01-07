package com.example.litecalendar.domain.model

import android.os.Parcelable
import com.example.litecalendar.presentation.home_screen.CalenderUiEvents
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

data class Event (
    val eventId : String,
    val notificationId: String,
    val userId : String = "null",
    val title : String,
    val type : String = "event",
    val startDate : LocalDate,
    val endDate : LocalDate,
    val startTime : String,
    val endTime: String,
    val description : String,
    val isAllDay : Boolean,
    val alertTime : Int,
    val isNotificationEnabled : Boolean = false,
    val repeatOption : String = "DOES_NOT_REPEAT"
)