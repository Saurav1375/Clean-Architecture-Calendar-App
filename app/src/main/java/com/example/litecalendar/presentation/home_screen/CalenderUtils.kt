package com.example.litecalendar.presentation.home_screen

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.litecalendar.domain.model.Event
import com.example.litecalendar.domain.model.Holiday
import com.example.litecalendar.utils.Constants
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
data class CalenderState (
    val events: List<Event> = emptyList(),
    val holidays : List<Holiday> = emptyList(),
    val loading : Boolean = true,
    val calender: CalendarData = CalendarData(),
    val query: String = "",
    val error : String? = null,
    val page : Int = Constants.INITIAL_PAGE,
    val date : LocalDate = LocalDate.now()
)

data class CalendarData(
    val currentMonthDates: List<LocalDate> = emptyList(),
    val previousMonthDates: List<LocalDate> = emptyList(),
    val nextMonthDates: List<LocalDate> = emptyList()
)
object CalendarUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun initializeCalendar(monthOffset: Int): CalendarData {
        val today = LocalDate.now()
        val currentMonth = today.plusMonths(monthOffset.toLong())

        return CalendarData(
            currentMonthDates = generateDatesForMonth(currentMonth),
            previousMonthDates = generateDatesForMonth(currentMonth.minusMonths(1)),
            nextMonthDates = generateDatesForMonth(currentMonth.plusMonths(1))
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateDatesForMonth(month: LocalDate): List<LocalDate> {
        return (1..month.lengthOfMonth()).map { day ->
            month.withDayOfMonth(day)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getVisibleDates(calendar: CalendarData): List<LocalDate> {
        return calendar.run {
            val previousMonthLastMondayIndex = previousMonthDates.indexOf(previousMonthDates.last { it.dayOfWeek.value == 1 })
            val nextMonthCalendarRequiredSize = 42 - (currentMonthDates +previousMonthDates.subList(previousMonthLastMondayIndex, previousMonthDates.size)).size
                previousMonthDates.subList(previousMonthLastMondayIndex, previousMonthDates.size)+
                    currentMonthDates +
                    nextMonthDates.take(nextMonthCalendarRequiredSize)
        }
    }
}