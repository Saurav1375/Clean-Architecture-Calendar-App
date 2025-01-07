package com.example.litecalendar.data.remote.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Datetime(
    val day: Int,
    val month: Int,
    val year: Int
) : Parcelable