package com.example.litecalendar.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.litecalendar.data.firebase.dto.EventDto
import com.example.litecalendar.domain.model.Event
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

@RequiresApi(Build.VERSION_CODES.O)
fun Event.toEventDto(): EventDto {
    return EventDto(
        eventId,
        notificationId,
        userId,
        title,
        type,
        startDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        endDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
        startTime,
        endTime,
        description,
        isAllDay,
        alertTime,
        isNotificationEnabled,
        repeatOption
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun EventDto.toEvent(): Event {
    return Event(
        eventId,
        notificationId,
        userId,
        title,
        type,
        Instant.ofEpochMilli(startDate)
            .atZone(ZoneId.systemDefault()).toLocalDate(),
        Instant.ofEpochMilli(endDate)
            .atZone(ZoneId.systemDefault()).toLocalDate(),
        startTime,
        endTime,
        description,
        isAllDay,
        alertTime,
        isNotificationEnabled,
        repeatOption
    )
}


