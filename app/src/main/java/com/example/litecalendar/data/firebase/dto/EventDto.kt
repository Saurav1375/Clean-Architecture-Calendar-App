package com.example.litecalendar.data.firebase.dto

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@RequiresApi(Build.VERSION_CODES.O)
data class EventDto (
    val eventId: String = UUID.randomUUID().toString(),
    val notificationId : String = "",
    val userId : String = "",
    val title : String = "",
    val type : String = "event",
    val startDate : Long = 0L,
    val endDate : Long = 0L,
    val startTime : String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
    val endTime: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
    val description : String = "",
    val isAllDay : Boolean = false,
    val alertTime : Int = 0,
    val isNotificationEnabled : Boolean = false,
    val repeatOption : String = "DOES_NOT_REPEAT",
){
    companion object {
        fun fromFirestore(document: DocumentSnapshot): EventDto {
            return try {
                val data = document.data
                EventDto(
                    eventId = data?.get("eventId") as? String ?: "",
                    notificationId = data?.get("notificationId") as? String ?: "",
                    userId = data?.get("userId") as? String ?: "",
                    title = data?.get("title") as? String ?: "",
                    type = data?.get("type") as? String ?: "event",
                    startDate = (data?.get("startDate") as? Long) ?: 0L,
                    endDate = (data?.get("endDate") as? Long) ?: 0L,
                    startTime = data?.get("startTime") as? String
                        ?: LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                    endTime = data?.get("endTime") as? String
                        ?: LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                    description = data?.get("description") as? String ?: "",
                    isAllDay = when (val value = data?.get("allDay")) {
                        is Boolean -> value
                        is Long -> value == 1L
                        is Int -> value == 1
                        is String -> value.lowercase() == "true"
                        else -> false
                    },
                    alertTime = (data?.get("alertTime") as? Long)?.toInt() ?: 0,
                    isNotificationEnabled = when (val value = data?.get("notificationEnabled")) {
                        is Boolean -> value
                        is Long -> value == 1L
                        is Int -> value == 1
                        is String -> value.lowercase() == "true"
                        else -> false
                    },
                    repeatOption = data?.get("repeatOption") as? String ?: "DOES_NOT_REPEAT"
                )
            } catch (e: Exception) {
                Log.e("EventDto", "Error converting document: ${e.message}")
                EventDto()
            }
        }
    }
}


