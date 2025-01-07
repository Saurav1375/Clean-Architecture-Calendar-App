package com.example.litecalendar.presentation.home_screen.components.month_view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.litecalendar.domain.model.Event
import com.example.litecalendar.domain.model.Holiday
import com.example.litecalendar.presentation.home_screen.CalenderState
import com.example.litecalendar.presentation.home_screen.components.RepeatMode
import com.example.litecalendar.presentation.home_screen.components.WeekdayHeader
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarGrid(
    navController : NavHostController,
    pagerState: PagerState,
    state: CalenderState,
    dates: List<LocalDate>,
    events: List<Event>,
    holidays: List<Holiday>,
    monthOffset: Int,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
//        MonthHeader(monthOffset = monthOffset)
        WeekdayHeader(monthOffset)
        if (state.loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

        }
        val eventMap = remember(events) { events.associateBy { it.startDate } }
        val holidayMap = remember(holidays) {
            holidays.associateBy {
                LocalDate.parse(it.date.iso.substringBefore("T"))
            }
        }

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            items(dates, key = { it.toString() }) { date ->
                CalendarBox(
                    navController = navController,
                    date = date,
                    event = events.filter {
                        when (it.repeatOption) {

                            RepeatMode.DOES_NOT_REPEAT.toString() -> it.startDate == date
                            RepeatMode.EVERYMONTH.toString() -> it.startDate.dayOfMonth == date.dayOfMonth
                            RepeatMode.EVERYDAY.toString() -> true
                            RepeatMode.EVERYWEEK.toString() -> it.startDate.dayOfWeek == date.dayOfWeek
                            RepeatMode.EVERYYEAR.toString() -> (it.startDate.dayOfMonth == date.dayOfMonth) && (it.startDate.monthValue == date.monthValue)
                            else -> false

                        }
//
                    },
                    holiday = holidays.filter { LocalDate.parse(it.date.iso.substringBefore("T")) == date }
                        .distinctBy { it.name }, // Fast lookup for holidays
                    onDateClick = { onDateClick(date) }
                )
            }
        }
    }
}