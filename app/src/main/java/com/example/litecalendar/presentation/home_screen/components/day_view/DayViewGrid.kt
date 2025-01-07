package com.example.litecalendar.presentation.home_screen.components.day_view


import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.litecalendar.domain.model.Event
import com.example.litecalendar.domain.model.Holiday
import com.example.litecalendar.domain.model.toEventParcel
import com.example.litecalendar.presentation.home_screen.CalenderState
import com.example.litecalendar.presentation.home_screen.CalenderUiEvents
import com.example.litecalendar.presentation.home_screen.HomeScreenViewModel
import com.example.litecalendar.presentation.home_screen.Screen
import com.example.litecalendar.presentation.home_screen.components.DateCircle
import com.example.litecalendar.presentation.home_screen.components.EventBox
import com.example.litecalendar.presentation.home_screen.components.HolidayBox
import com.example.litecalendar.presentation.home_screen.components.toSheetState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayViewGrid(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    state: CalenderState,
    date: LocalDate,
    events: List<Event?>,
    holidays: List<Holiday?>,
    dateOffset: Int,
    navController: NavHostController,
    startHour: Int = 1,
    endHour: Int = 23,
) {
    val hourHeight = 70.dp
    val isToday = date == LocalDate.now()


    fun timeToFloat(time: String): Float {
        val parts = time.split(":")
        return parts[0].toFloat() + parts[1].toFloat() / 60
    }

    // Check if events overlap in time
    fun eventsOverlap(event1: Event, event2: Event): Boolean {
        val start1 = timeToFloat(event1.startTime)
        val end1 = timeToFloat(event1.endTime)
        val start2 = timeToFloat(event2.startTime)
        val end2 = timeToFloat(event2.endTime)
        return !(end1 <= start2 || start1 >= end2)
    }

    // Find all events that overlap with a given event
    fun findOverlappingEvents(event: Event, allEvents: List<Event>): List<Event> {
        return allEvents.filter { other ->
            event != other && eventsOverlap(event, other)
        }
    }

    // Calculate column position for each event
    fun calculateEventLayout(events: List<Event>): Map<Event, Pair<Int, Int>> {
        val layout = mutableMapOf<Event, Pair<Int, Int>>() // Event to (column, totalColumns)

        events.forEach { event ->
            if (!layout.containsKey(event)) {
                val overlappingEvents = findOverlappingEvents(event, events)
                val allEventsInGroup =
                    (overlappingEvents + event).sortedBy { timeToFloat(it.startTime) }

                // Assign positions for all events in the overlapping group
                allEventsInGroup.forEachIndexed { index, groupEvent ->
                    layout[groupEvent] = Pair(index, allEventsInGroup.size)
                }
            }
        }

        return layout
    }

    Box(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.fillMaxSize(),

            ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .height(75.dp)
            )
            {

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            if (isToday) 2.dp else 0.dp
                        )
                    ) {
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("EEE")),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isToday)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onBackground
                        )
                        DateCircle(date = date, size = 40.dp)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        events.filter { it?.isAllDay != false }.forEach {
                            it?.let {
                                EventBox(
                                    navController = navController,
                                    event = it,
                                    textPadding = 8.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(25.dp)
                                        .padding(start = 16.dp)
                                        .clickable {
                                            val itemParcel = it.toEventParcel()
                                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                                "itemParcel",
                                                itemParcel
                                            )
                                            navController.navigate(Screen.MainItemViewScreen.route + "/$itemParcel")

                                        },

                                    corner = 9.dp

                                )
                            }
                        }

                        holidays.distinctBy { it?.name ?: "" }.forEach {
                            it?.let {
                                HolidayBox(
                                    holiday = it, textPadding = 8.dp, modifier = Modifier
                                        .fillMaxWidth()
                                        .height(25.dp)
                                        .padding(start = 16.dp)
                                        .clickable {
                                            val itemParcel = it
                                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                                "itemParcel",
                                                itemParcel
                                            )
                                            navController.navigate(Screen.MainItemViewScreen.route + "/$itemParcel")
                                        }
                                    , corner = 9.dp,

                                )
                            }

                        }

                    }

                }

            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    (startHour..endHour).forEach { hour ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = hourHeight * (hour - startHour + 1))
                                .height(hourHeight)
                        ) {
                            // Vertical time separator
                            VerticalDivider(
                                modifier = Modifier
                                    .padding(start = 64.dp)
                                    .fillMaxHeight()
                            )

                            // Time label and horizontal divider
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .fillMaxSize()
                                    .padding(start = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TimeLabel(hour = hour)
                                HourDivider(hour = hour)
                            }
                        }
                    }
                }


                val nonNullEvents = events.filterNotNull().filter { !it.isAllDay }
                val eventLayout = calculateEventLayout(nonNullEvents)

                nonNullEvents.forEach { event ->
                    val (column, totalColumns) = eventLayout[event] ?: Pair(0, 1)
                    val startTime = timeToFloat(event.startTime)
                    val endTime = timeToFloat(event.endTime)
                    val height = hourHeight * (endTime - startTime)

                    val availableWidth = LocalConfiguration.current.screenWidthDp.dp - 64.dp
                    val columnWidth = availableWidth / totalColumns
                    val startPadding = 64.dp + (columnWidth * column)

                    EventContainer(
                        item = event,
                        navController = navController,
                        modifier = Modifier
                            .padding(
                                start = startPadding,
                                top = hourHeight * startTime,
                                end = if (column == totalColumns - 1) 4.dp else 2.dp
                            )
                            .width(columnWidth - 4.dp)  // Subtract padding for spacing
                            .height(if (height <= 0.dp || event.type == "TASK") 25.dp else height)
                    )
                }

                var sliderOffset by remember { mutableStateOf(0.dp) }

                val currentTime = LocalTime.now()
                val hours = currentTime.hour
                val minutes = currentTime.minute
                val timePadding = hours + (minutes / 60f)

                // Observe changes in the current page and update the slider offset
                LaunchedEffect((date == LocalDate.now())) {
                    sliderOffset = hourHeight * (timePadding - startHour + 1) - 23.dp
                }


                if (date == LocalDate.now()) {
                    Slider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 56.dp,
                                top = sliderOffset.coerceAtLeast(0.dp) // Prevent negative offset
                            ),
                        value = 0f,
                        enabled = false,
                        onValueChange = {},
                        colors = SliderDefaults.colors(
                            disabledThumbColor = MaterialTheme.colorScheme.onBackground,
                            disabledInactiveTrackColor = MaterialTheme.colorScheme.onBackground,
                            disabledActiveTrackColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }


            }

        }

    }
}


@Composable
private fun TimeLabel(
    hour: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = String.format("%02d:00", hour),
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun HourDivider(
    hour: Int,
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier.fillMaxWidth(
            if (hour >= 10) 0.98f else 0.96f
        )
    )
}

@SuppressLint("NewApi")
@Composable
private fun EventContainer(
    item: Any,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(horizontal = 2.dp) // Add some spacing between events
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                if (item is Event) {
                    println("item : $item")
                    val itemParcel = item.toEventParcel()
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "itemParcel",
                        itemParcel
                    )
                    println("item : $itemParcel")
                    navController.navigate(Screen.MainItemViewScreen.route + "/$itemParcel")


                }


            }
            .background(
                when (item) {
                    is Event -> if (item.type == "EVENT") {
                        MaterialTheme.colorScheme.tertiary
                    } else {
                        MaterialTheme.colorScheme.error
                    }

                    is Holiday -> MaterialTheme.colorScheme.outline
                    else -> Color.Unspecified
                }
            )
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary),
                RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            text = when (item) {
                is Event -> item.title
                is Holiday -> item.name
                else -> "(No title)"
            },
            style = MaterialTheme.typography.bodySmall,
            color = when (item) {
                is Event -> if (item.type == "EVENT") {
                    MaterialTheme.colorScheme.onTertiary
                } else {
                    MaterialTheme.colorScheme.onError
                }

                is Holiday -> MaterialTheme.colorScheme.onBackground
                else -> Color.Unspecified
            }
        )
    }
}
