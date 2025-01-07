package com.example.litecalendar.data.repository

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.litecalendar.data.auth.AuthRepositoryImpl
import com.example.litecalendar.data.mapper.toEventDto
import com.example.litecalendar.data.firebase.dto.EventDto
import com.example.litecalendar.domain.model.Event
import com.example.litecalendar.domain.repository.EventService
import com.example.litecalendar.utils.Constants
import com.example.litecalendar.utils.Resource
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class EventServiceImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val context : Application
) : EventService {

    private val currentUser = auth.currentUser

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun addEvent(event: EventDto) : Resource<Unit> {
        return try {
            db.collection("Users")
                .document(currentUser?.email.toString())
                .collection("ITEMS")
                .document(event.eventId)
                .set(event)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Error : ${e.message}")
            Resource.Error(message = e.message.toString(), null)

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getAllEvent(type: String): Flow<Resource<List<EventDto>>> = callbackFlow {
        trySend(Resource.Loading(isLoading = true))
        try {
            val querySnapshot = db.collection("Users")
                .document(currentUser?.email.toString())
                .collection(type)
                .get()
                .await()

            val eventsList = querySnapshot.documents.mapNotNull { document ->
                EventDto.fromFirestore(document)
            }

            Log.e(Constants.TAG, eventsList.toString())
            trySend(Resource.Success(eventsList))
        } catch (e: Exception) {
            trySend(Resource.Error(message = e.message.toString(), data = null))
            Log.e(Constants.TAG, "Error: ${e.message}")
        }
        awaitClose()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getEventsForMonth(monthOffset: Int, type: String): Flow<Resource<List<EventDto>>> = callbackFlow {
        val currentDate = LocalDate.now().plusMonths(monthOffset.toLong())
        val startOfMonth = currentDate.minusMonths(1L).withDayOfMonth(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() // Convert to Long (milliseconds since epoch)
        val endOfMonth = currentDate.plusMonths(1L).withDayOfMonth(currentDate.plusMonths(1L).lengthOfMonth())
            .atTime(23, 59, 59)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli() // Convert to Long (milliseconds since epoch)
        trySend(Resource.Loading(isLoading = true))
        val listener = db.collection("Users")
            .document(currentUser?.email.toString())
            .collection(type)
            .whereGreaterThanOrEqualTo("startDate", startOfMonth)
            .whereLessThanOrEqualTo("startDate", endOfMonth)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(Constants.TAG, "Error : ${error.message.toString()}")
                    trySend(Resource.Error("Error: ${error.message.toString()}"))
                    close(error)
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(EventDto::class.java)
                } ?: emptyList()
                Log.d(Constants.TAG, events.toString())
                trySend(Resource.Success(events))
            }

        awaitClose { listener.remove() }
    }



        override suspend fun deleteEvent(event: EventDto) : Resource<Unit> {
            return try {
                db.collection("Users")
                    .document(currentUser?.email.toString())
                    .collection("ITEMS")
                    .document(event.eventId)
                    .delete()
                    .await()
                Resource.Success(Unit)
            } catch (e: Exception) {
                Log.e(Constants.TAG, "Error : ${e.message}")
                Resource.Error(message = e.message.toString(), null)

            }

        }
    }