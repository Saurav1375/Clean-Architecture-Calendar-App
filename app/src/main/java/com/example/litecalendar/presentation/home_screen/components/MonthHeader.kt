package com.example.litecalendar.presentation.home_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthHeader(monthOffset: Int, dateView : Boolean = false) {
    val currentMonth = if(!dateView) LocalDate.now().plusMonths(monthOffset.toLong()) else LocalDate.now().plusDays(monthOffset.toLong())
    val monthYear = currentMonth.format(
        DateTimeFormatter.ofPattern("MMMM yyyy")
    )

    Text(
        text = monthYear,
        modifier = Modifier
            .widthIn(max = 72.dp),
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis

    )
}