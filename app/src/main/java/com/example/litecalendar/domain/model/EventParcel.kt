package com.example.litecalendar.domain.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.example.litecalendar.presentation.home_screen.CalenderUiEvents
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class EventParcel(
    val eventId: String,
    val notificationId: String,
    val userId: String,
    val title: String,
    val type: String = "event",
    val startDate: String,
    val endDate: String,
    val startTime: String,
    val endTime: String,
    val description: String,
    val isAllDay: Boolean,
    val alertTime: Int,
    val isNotificationEnabled: Boolean = false,
    val repeatOption: String = "DOES_NOT_REPEAT"
) : Parcelable


fun Event.toEventParcel(): EventParcel {
    return EventParcel(
        eventId,
        notificationId,
        userId,
        title,
        type,
        startDate.toString(),
        endDate.toString(),
        startTime, endTime, description, isAllDay, alertTime, isNotificationEnabled, repeatOption
    )
}

@SuppressLint("NewApi")
fun EventParcel.toEvent(): Event {
    return Event(
        eventId,
        notificationId,
        userId,
        title,
        type,
        LocalDate.parse(startDate),
        LocalDate.parse(endDate),
        startTime, endTime, description, isAllDay, alertTime, isNotificationEnabled, repeatOption
    )
}