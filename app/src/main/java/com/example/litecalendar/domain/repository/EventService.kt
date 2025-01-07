package com.example.litecalendar.domain.repository

import com.example.litecalendar.data.firebase.dto.EventDto
import com.example.litecalendar.domain.model.Event
import com.example.litecalendar.utils.Resource
import kotlinx.coroutines.flow.Flow

interface EventService {
    suspend fun addEvent(event : EventDto) : Resource<Unit>
    suspend fun deleteEvent(event: EventDto) : Resource<Unit>
    suspend fun getAllEvent(type: String) : Flow<Resource<List<EventDto>>>
    suspend fun getEventsForMonth(monthOffset: Int, type : String): Flow<Resource<List<EventDto>>>
}