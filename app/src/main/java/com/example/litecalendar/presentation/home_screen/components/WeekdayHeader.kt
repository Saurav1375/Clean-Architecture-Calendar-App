package com.example.litecalendar.presentation.home_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeekdayHeader(monthOffset : Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.spacedBy(LocalConfiguration.current.screenWidthDp.dp / 7)
    ) {
        val weekdays = listOf("M", "T", "W", "T", "F", "S", "S")
        weekdays.forEachIndexed { index, day ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp / 7)
            ) {
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = if (LocalDate.now().dayOfWeek.value == index + 1 && monthOffset == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
            }

        }
    }
}