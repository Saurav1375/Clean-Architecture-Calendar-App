package com.example.litecalendar.presentation.home_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

enum class ValueType {
    EVENT, TASK
}

enum class RepeatMode {
    DOES_NOT_REPEAT,
    EVERYDAY,
    EVERYWEEK,
    EVERYMONTH,
    EVERYYEAR
}

enum class Before {
    FIVE,
    TEN,
    FIFTEEN,
    THIRTY,
    ONE_HOUR,
    ONE_DAY
}

@RequiresApi(Build.VERSION_CODES.O)
data class SheetState(
    val eventId : String = UUID.randomUUID().toString(),
    val notificationId : String = "",
    val isSheetVisible: Boolean = false,
    val title: String = "",
    val description : String = "",
    val isAllDay: Boolean = false,
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
    val showRepeatPicker: Boolean = false,
    val isNotificationEnabled : Boolean = false,
    val showNotificationPicker: Boolean = false,
    val startDate: Long = LocalDate.now().atStartOfDay(ZoneId.systemDefault()) // Convert to start of day in the system's default time zone
        .toInstant() // Convert to Instant
        .toEpochMilli(),
    val endDate: Long = LocalDate.now().atStartOfDay(ZoneId.systemDefault()) // Convert to start of day in the system's default time zone
        .toInstant() // Convert to Instant
        .toEpochMilli(),
    val startTime: String = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
    val endTime: String = LocalTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("HH:mm")),
    val type: ValueType = ValueType.EVENT,
    val selectedRadioOption : RepeatMode = RepeatMode.DOES_NOT_REPEAT,
    val selectedNotificationOption : Before = Before.THIRTY
)


fun getRepeatTitle(repeatMode: RepeatMode) : String {
    return when (repeatMode) {
        RepeatMode.DOES_NOT_REPEAT -> "Does not Repeat"
        RepeatMode.EVERYDAY -> "Every Day"
        RepeatMode.EVERYWEEK -> "Every Week"
        RepeatMode.EVERYMONTH -> "Every Month"
        RepeatMode.EVERYYEAR -> "Every Year"
    }
}

fun getRepeatText(repeatMode: String) : String {
    return when (repeatMode) {
        "DOES_NOT_REPEAT" -> "Does not Repeat"
        "EVERYDAY" -> "Repeats Daily"
        "EVERYWEEK" -> "Repeats Weekly"
        "EVERYMONTH" -> "Repeats Monthly"
        "EVERYYEAR" -> "Repeats Yearly"
        else -> ""
    }
}


fun getNotificationText(time : Int): String {
    return when (time) {
        5 -> "5 minutes before"
        10 -> "10 minutes before"
        15 -> "15 minutes before"
        30 -> "30 minutes before"
        60 -> "1 hour before"
        60 * 24 -> "1 day before"
        else -> ""
    }
}


fun getNotificationDescription(before: Before): String {
    return when (before) {
        Before.FIVE -> "5 minutes before"
        Before.TEN -> "10 minutes before"
        Before.FIFTEEN -> "15 minutes before"
        Before.THIRTY -> "30 minutes before"
        Before.ONE_HOUR -> "1 hour before"
        Before.ONE_DAY -> "1 day before"
    }
}