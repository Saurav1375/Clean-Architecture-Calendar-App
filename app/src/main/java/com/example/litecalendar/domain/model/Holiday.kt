package com.example.litecalendar.domain.model

import android.os.Parcelable
import com.example.litecalendar.data.remote.dto.Country
import com.example.litecalendar.data.remote.dto.Date
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class Holiday(
     val country: Country,
    val date: Date,
    val description: String,
    val locations: String,
    val name: String,
    val primaryType: String,
    val states: String,
    val type: List<String>
) : Parcelable

