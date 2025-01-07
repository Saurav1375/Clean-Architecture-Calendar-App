package com.example.litecalendar.presentation.home_screen.components.month_view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.litecalendar.domain.model.Event
import com.example.litecalendar.domain.model.Holiday
import com.example.litecalendar.presentation.home_screen.components.DateCircle
import com.example.litecalendar.presentation.home_screen.components.EventBox
import com.example.litecalendar.presentation.home_screen.components.HolidayBox
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarBox(
    navController: NavHostController,
    date: LocalDate,
    event: List<Event?>,
    holiday: List<Holiday?>,
    onDateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(1f / 7f)
            .height(height = LocalConfiguration.current.screenHeightDp.dp / 7)
            .border(BorderStroke((0.3).dp, MaterialTheme.colorScheme.tertiaryContainer))
            .clickable(onClick = onDateClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 2.dp)
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            DateCircle(date = date)

            event.forEach{ event ->
                event?.let { EventBox(event = it, navController = navController, enable = false) }
            }
            holiday.forEach { holiday ->
                holiday?.let { HolidayBox(holiday = it) }
            }
        }
    }
}