package com.example.litecalendar.data.remote.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Date(
    val datetime: Datetime,
    val iso: String
) : Parcelable