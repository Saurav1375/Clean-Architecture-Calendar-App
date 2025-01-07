package com.example.litecalendar.presentation.home_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.NotificationAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.WorkManager
import coil.compose.AsyncImage
import com.example.litecalendar.R
import com.example.litecalendar.data.auth.UserData
import com.example.litecalendar.data.notifications.schedulePeriodicNotification
import com.example.litecalendar.data.notifications.scheduleSingleNotification
import com.example.litecalendar.presentation.home_screen.CalenderUiEvents
import com.example.litecalendar.presentation.home_screen.HomeScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.chrono.Chronology
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

data class UserItem(
    val id: Int = 0,
    val title: String,
    val type: ValueType
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomModalSheet(
    isSheetVisible: Boolean,
    viewModel: HomeScreenViewModel,
    isDateView: Boolean = false,
    date: LocalDate = LocalDate.now(),
    update: Boolean = false,
    user: UserData?,
    updateSection: () -> Unit = {}

) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = !isDateView,
    )
    val context = LocalContext.current


    val currentDateMillis = LocalDate.now()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = currentDateMillis)
    val itemList = listOf(
        UserItem(0, "Event", ValueType.EVENT),
        UserItem(1, "Task", ValueType.TASK)
    )

    val startOrEnd = rememberSaveable {
        mutableStateOf(1)

    }
    val sheetFields by viewModel.sheetUIState


    if (sheetFields.showDatePicker) {
        DatePickerModal(
            datePickerState,
            onConfirmClick = {
                viewModel.onEvent(
                    CalenderUiEvents.OnDateSelected(
                        datePickerState.selectedDateMillis,
                        startOrEnd.value
                    )
                )
            }
        ) {
            viewModel.onEvent(CalenderUiEvents.OnDatePickerVisible(false))
        }
    }

    if (sheetFields.showTimePicker) {
        AdvancedTimePicker(onConfirm = {
            viewModel.onEvent(
                CalenderUiEvents.OnTImeSelected(
                    "${it.hour}:${it.minute}", startOrEnd.value
                )
            )

        }) {
            viewModel.onEvent(CalenderUiEvents.OnTimePickerVisible(false))
        }
    }

    if (sheetFields.showRepeatPicker) {
        ItemsDialog(sheetFields = sheetFields, viewModel = viewModel) {
            viewModel.onEvent(CalenderUiEvents.OnRepeatDialogVisible(false))
        }
    }

    if (sheetFields.showNotificationPicker) {
        NotificationDialog(sheetFields = sheetFields, viewModel = viewModel) {
            viewModel.onEvent(CalenderUiEvents.OnNotificationDialogVisible(false))
        }
    }


    if (isSheetVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                viewModel.onEvent(CalenderUiEvents.OnSheetVisible(false))
            },
            tonalElevation = 4.dp,
            containerColor = MaterialTheme.colorScheme.inverseOnSurface,
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        viewModel.onEvent(CalenderUiEvents.OnSheetVisible(false))
                        viewModel.onEvent(CalenderUiEvents.OnDiscard)

                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear"
                        )

                    }

                    Button(onClick = {
                        if(update && sheetFields.type == ValueType.EVENT && sheetFields.isNotificationEnabled && sheetFields.notificationId.isNotEmpty()){
                            WorkManager.getInstance(context).cancelWorkById(UUID.fromString(sheetFields.notificationId))
                        }
                        val message = if (sheetFields.isAllDay) {
                            formatLongDate(sheetFields.startDate)
                        } else {
                            if(sheetFields.selectedNotificationOption == Before.ONE_DAY){
                                "Tomorrow ${sheetFields.startTime} - ${sheetFields.endDate}"
                            }
                            "${sheetFields.startTime} - ${sheetFields.endTime}"
                        }
                        val delay = if (!sheetFields.isAllDay) calculateNotificationDelay(
                            sheetFields.startDate,
                            sheetFields.startTime
                        ) else calculateNotificationDelay(sheetFields.startDate, "00:00")
                        println("Working $delay")
                        if (sheetFields.isNotificationEnabled && delay > 0 && sheetFields.type == ValueType.EVENT ) {
                            val timeLeft = maxOf(0L, delay - getAlertTime(sheetFields.selectedNotificationOption) * 60 * 1000L)

                           val notificationId =  if (sheetFields.selectedRadioOption == RepeatMode.DOES_NOT_REPEAT) {

                                scheduleSingleNotification(
                                    context,
                                    timeLeft,
                                    title = sheetFields.title.ifEmpty { "(No Title)" },
                                    message = message
                                )
                            }

                            else {
                                schedulePeriodicNotification(
                                    context,
                                    intervalHours = getIntervalInHours(sheetFields.selectedRadioOption),
                                    initialDelay = timeLeft,
                                    title = sheetFields.title.ifEmpty { "(No Title)" },
                                    message = message

                                )
                            }
                            viewModel.onEvent(CalenderUiEvents.SetNotificationId(notificationId))
                        }
                        viewModel.onEvent(CalenderUiEvents.OnSaveButtonClicked(update))
                        updateSection()
                        viewModel.onEvent(CalenderUiEvents.OnSheetVisible(false))
                    }) {
                        Text(text = if (!update) "Save" else "Update")
                    }

                }
                Spacer(modifier = Modifier.height(8.dp))


                BasicTextField(
                    modifier = Modifier.padding(start = 64.dp),
                    value = sheetFields.title,
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    onValueChange = {
                        viewModel.onEvent(CalenderUiEvents.OnTitleChange(it))
                    },
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                    decorationBox = { innerTextField ->
                        innerTextField()
                        if (sheetFields.title.isEmpty()) {
                            Text(
                                modifier = Modifier.padding(start = 2.dp, top = 2.dp),
                                text = "Add title",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }


                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(
                    modifier = Modifier.padding(start = 64.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    items(itemList) {
                        ItemBox(
                            item = it,
                            isSelected = it.type == sheetFields.type
                        ) { item ->
                            viewModel.onEvent(CalenderUiEvents.OnItemSelected(item.type))
                        }
                    }


                }
                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                    ) {

                        AsyncImage(
                            model = user?.profilePictureUrl,
                            contentDescription = "userPic",
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiary)
                            )
                            Text(
                                modifier = Modifier.padding(start = 6.dp),
                                text = sheetFields.type.name.lowercase()
                                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        user?.emailId?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }


                    }


                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(Modifier.fillMaxWidth())

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clickable { },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Row(
                            modifier = Modifier.padding(start = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                modifier = Modifier,
                                imageVector = Icons.Default.Timer,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(32.dp))

                            Text(
                                modifier = Modifier,
                                text = "All-day",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                        }

                        Switch(
                            modifier = Modifier.padding(end = 22.dp),
                            checked = sheetFields.isAllDay,
                            onCheckedChange = {
                                viewModel.onEvent(CalenderUiEvents.IsAllDayToggle(it))
                            },
                            thumbContent = if (sheetFields.isAllDay) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            } else {
                                null
                            }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Row(
                            modifier = Modifier
                                .padding(start = 76.dp)
                                .clickable {
                                    startOrEnd.value = 0
                                    viewModel.onEvent(CalenderUiEvents.OnDatePickerVisible(true))
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Format the date
                            val formattedStartDate = sheetFields.startDate.let {
                                val localDate = Instant.ofEpochMilli(it)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                val formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy")
                                localDate.format(formatter)
                            } ?: "No date selected"
                            Text(
                                modifier = Modifier,
                                text = formattedStartDate,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )


                        }
                        if (!sheetFields.isAllDay) {
                            Text(
                                modifier = Modifier
                                    .padding(end = 22.dp)
                                    .clickable {
                                        startOrEnd.value = 0
                                        viewModel.onEvent(
                                            CalenderUiEvents.OnTimePickerVisible(
                                                true
                                            )
                                        )
                                    },
                                text = sheetFields.startTime,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                    }
                    Spacer(modifier = Modifier.height(4.dp))



                    if (sheetFields.type == ValueType.EVENT) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Row(
                                modifier = Modifier
                                    .padding(start = 76.dp)
                                    .clickable {
                                        startOrEnd.value = 1
                                        viewModel.onEvent(
                                            CalenderUiEvents.OnDatePickerVisible(true)
                                        )
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {

                                // Format the date
                                val formattedEndDate = sheetFields.endDate.let {
                                    val localDate = Instant.ofEpochMilli(it)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                    val formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy")
                                    localDate.format(formatter)
                                } ?: "No date selected"
                                Text(
                                    modifier = Modifier,
                                    text = formattedEndDate,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                            }
                            if (!sheetFields.isAllDay) {
                                Text(
                                    modifier = Modifier
                                        .padding(end = 22.dp)
                                        .clickable {
                                            startOrEnd.value = 1
                                            viewModel.onEvent(
                                                CalenderUiEvents.OnTimePickerVisible(
                                                    true
                                                )
                                            )
                                        },
                                    text = sheetFields.endTime,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                        }
                    }


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clickable {
                                viewModel.onEvent(CalenderUiEvents.OnRepeatDialogVisible(true))
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Row(
                            modifier = Modifier.padding(start = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                modifier = Modifier,
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(32.dp))

                            Text(
                                modifier = Modifier,
                                text = getRepeatTitle(sheetFields.selectedRadioOption),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                        }

                    }

                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(Modifier.fillMaxWidth())
                if (sheetFields.type == ValueType.EVENT) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .clickable {
                                viewModel.onEvent(CalenderUiEvents.OnNotificationDialogVisible(true))
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Row(
                            modifier = Modifier.padding(start = 20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                modifier = Modifier,
                                imageVector = Icons.Default.NotificationAdd,
                                contentDescription = null
                            )


                            Spacer(modifier = Modifier.width(32.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween

                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = if (sheetFields.isNotificationEnabled) getNotificationDescription(
                                        sheetFields.selectedNotificationOption
                                    ) else "Add notification",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                if (sheetFields.isNotificationEnabled) {
                                    IconButton(
                                        modifier = Modifier.padding(end = 8.dp),
                                        onClick = {
                                            viewModel.onEvent(CalenderUiEvents.DisableNotifications)
                                        }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }

                        }


                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(Modifier.fillMaxWidth())
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.padding(start = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.Default.Description,
                        contentDescription = null
                    )
                    BasicTextField(
                        modifier = Modifier.padding(start = 32.dp),
                        value = sheetFields.description,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        onValueChange = {
                            viewModel.onEvent(CalenderUiEvents.OnDescChange(it))
                        },
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                        decorationBox = { innerTextField ->
                            innerTextField()
                            if (sheetFields.description.isEmpty()) {
                                Text(
                                    modifier = Modifier.padding(start = 2.dp, top = 2.dp),
                                    text = if (sheetFields.type == ValueType.EVENT) "Add Description" else "Add Details",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }


                    )


                }


            }

        }
    }


}

@Composable
fun ItemBox(item: UserItem, isSelected: Boolean, onClick: (UserItem) -> Unit) {
    Box(
        modifier = Modifier
            .padding(end = 16.dp)
            .widthIn(min = 70.dp)
            .height(38.dp)
            .clickable(
                onClick = {
                    onClick(item)
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .background(
                if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
            .border(
                BorderStroke(
                    width = 0.4.dp,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ),
                RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center

    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

    }

}


fun calculateNotificationDelay(eventDate: Long, eventTime: String): Long {
    try {
        // Parse the event time (String) to extract hours and minutes
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val time = timeFormat.parse(eventTime)
        println("time : $time")
        val calendarTime = Calendar.getInstance().apply {
            if (time != null) {
                this.time = time
            }
        }
        val eventHour = calendarTime.get(Calendar.HOUR_OF_DAY)
        println("hour : $eventHour")
        val eventMinute = calendarTime.get(Calendar.MINUTE)
        println("min : $eventMinute")


        // Set the event date and time in a Calendar object
        val eventCalendar = Calendar.getInstance().apply {
            timeInMillis = eventDate
            set(Calendar.HOUR_OF_DAY, eventHour)
            set(Calendar.MINUTE, eventMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Calculate the delay in milliseconds
        val currentTime = System.currentTimeMillis()
        println("${eventCalendar.timeInMillis}")
        val delay = eventCalendar.timeInMillis - currentTime

        return if (delay > 0) delay else 0 // Return 0 if the event is in the past
    } catch (e: Exception) {
        e.printStackTrace()
        return 0 // Return 0 in case of an error
    }
}

fun formatLongDate(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("EEE dd, yyyy", Locale.getDefault())
    val date = Date(timestamp)
    return dateFormat.format(date)
}

fun getIntervalInHours(repeatMode: RepeatMode): Long {
    return when (repeatMode) {
        RepeatMode.EVERYDAY -> 24 // 1 day
        RepeatMode.EVERYWEEK -> 7 * 24 // 7 days
        RepeatMode.EVERYMONTH -> 30 * 24 // Approximation: 30 days in a month
        RepeatMode.EVERYYEAR -> 365 * 24 // 365 days in a year
        else -> 0
    }
}
