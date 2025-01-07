package com.example.litecalendar.presentation.home_screen.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.litecalendar.data.firebase.dto.EventDto
import com.example.litecalendar.domain.model.Event
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


@RequiresApi(Build.VERSION_CODES.O)
fun SheetState.toEventDto() : EventDto {
    return EventDto(
        eventId = eventId,
        notificationId = this.notificationId,
        title = this.title.ifEmpty { "No title" },
        description = this.description,
        type = this.type.name,
        startDate = this.startDate,
        endDate = this.endDate,
        startTime = this.startTime,
        endTime = this.endTime,
        isAllDay = this.isAllDay,
        alertTime = getAlertTime(this.selectedNotificationOption),
        isNotificationEnabled = this.isNotificationEnabled,
        repeatOption = this.selectedRadioOption.name
    )
}

@SuppressLint("NewApi")
fun Event.toSheetState() : SheetState {
    return SheetState(
        eventId = eventId,
        notificationId = this.notificationId,
       title = if(this.title == "No title") "" else this.title,
        description = this.description,
        type = ValueType.valueOf(this.type),
        startDate = this.startDate.atStartOfDay(ZoneId.systemDefault()) // Set start of the day in system default time zone
            .toInstant()
            .toEpochMilli(),
        endDate = this.endDate.atStartOfDay(ZoneId.systemDefault()) // Set start of the day in system default time zone
            .toInstant()
            .toEpochMilli(),
        startTime = this.startTime,
        endTime = this.endTime,
        isAllDay = this.isAllDay,
        selectedNotificationOption = getNotificationValue(this.alertTime),
        isNotificationEnabled = this.isNotificationEnabled,
        selectedRadioOption = RepeatMode.valueOf(this.repeatOption)


    )
}



fun getAlertTime(before: Before): Int {
    return when (before) {
        Before.FIVE -> 5
        Before.TEN -> 10
        Before.FIFTEEN -> 15
        Before.THIRTY -> 30
        Before.ONE_HOUR -> 60
        Before.ONE_DAY -> 60 * 24
    }
}

fun getNotificationValue(value : Int): Before {
    return when (value) {
        5 -> Before.FIVE
        10 -> Before.TEN
        15 -> Before.FIFTEEN
        30 -> Before.THIRTY
        60 -> Before.ONE_HOUR
        60 * 24 -> Before.ONE_DAY
        else -> Before.FIVE
    }
}
