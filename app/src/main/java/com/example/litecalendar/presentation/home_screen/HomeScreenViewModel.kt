package com.example.litecalendar.presentation.home_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.litecalendar.data.mapper.toEvent
import com.example.litecalendar.data.mapper.toHoliday
import com.example.litecalendar.domain.model.Event
import com.example.litecalendar.domain.model.EventParcel
import com.example.litecalendar.domain.model.Holiday
import com.example.litecalendar.domain.model.toEvent
import com.example.litecalendar.domain.repository.EventService
import com.example.litecalendar.domain.repository.HolidayService
import com.example.litecalendar.presentation.home_screen.components.SheetState
import com.example.litecalendar.presentation.home_screen.components.toEventDto
import com.example.litecalendar.presentation.home_screen.components.toSheetState
import com.example.litecalendar.utils.Constants
import com.example.litecalendar.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject


data class QueryFilterResults(
    val events: List<Event> = emptyList(),
    val holidays: List<Holiday> = emptyList()
)

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val eventRepository: EventService,
    private val holidayRepository: HolidayService
) : ViewModel() {


    private val _state = MutableStateFlow(CalenderState())
    val state: StateFlow<CalenderState> = _state.asStateFlow()

    private val _sheetUIState = mutableStateOf(SheetState())
    val sheetUIState: State<SheetState> = _sheetUIState
    val snackBarState = SnackbarHostState()

    private val _filter = mutableStateOf(QueryFilterResults())
    val filter: State<QueryFilterResults> = _filter


    private var currentJob: Job? = null
    private var currentHolidayJob: Job? = null

    init {
        getAllEvents()
//        loadEventsForMonth(0)
        getHolidays()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: CalenderUiEvents) {
        when (event) {
            is CalenderUiEvents.OnPageChanged -> {
                _state.update {
                    it.copy(
                        page = event.page
                    )
                }
//                currentJob?.cancel()
//                currentJob = viewModelScope.launch {
//
//                    val monthOffset = event.page - Constants.INITIAL_PAGE
//                    loadEventsForMonth(monthOffset)
//                }
            }

            is CalenderUiEvents.OnSearch -> {

                if (state.value.query.isNotEmpty()) {

                    _filter.value = _filter.value.copy(
                        events = _state.value.events.filter {
                            it.title.contains(
                                _state.value.query,
                                ignoreCase = true
                            )
                        },
                        holidays = _state.value.holidays.filter {
                            it.name.contains(
                                _state.value.query,
                                ignoreCase = true
                            )
                        }
                    )
                } else {
                    _filter.value = QueryFilterResults()
                }


            }

            is CalenderUiEvents.OnSearchQueryChange -> {
                _state.update {
                    it.copy(
                        query = event.query
                    )
                }
            }

            is CalenderUiEvents.OnSheetVisible -> {
                if (savedStateHandle.get<EventParcel>("itemParcel") != null) {
                    _sheetUIState.value = _sheetUIState.value.copy(
                        isSheetVisible = event.isSheetVisible,
                        type = event.type
                    )
                } else {
                    _sheetUIState.value = SheetState(
                        isSheetVisible = event.isSheetVisible,
                        type = event.type
                    )
                }


            }

            is CalenderUiEvents.OnDatePickerVisible -> {
                _sheetUIState.value = _sheetUIState.value.copy(
                    showDatePicker = event.isDatePickerVisible,
                )
            }

            is CalenderUiEvents.OnDiscard -> {
                _sheetUIState.value = SheetState(
                    type = _sheetUIState.value.type
                )

            }

            is CalenderUiEvents.OnTimePickerVisible -> {
                _sheetUIState.value = _sheetUIState.value.copy(
                    showTimePicker = event.isTimePickerVisible,
                )

            }

            is CalenderUiEvents.OnRepeatDialogVisible -> {
                _sheetUIState.value = _sheetUIState.value.copy(
                    showRepeatPicker = event.isDialogVisible,
                )
            }


            is CalenderUiEvents.OnRadioOptionSelected -> {
                _sheetUIState.value = _sheetUIState.value.copy(
                    selectedRadioOption = event.option,
                )
            }


            is CalenderUiEvents.OnNotificationDialogVisible -> {
                _sheetUIState.value = _sheetUIState.value.copy(
                    showNotificationPicker = event.isDialogVisible,
                    isNotificationEnabled = true,
                )
            }


            is CalenderUiEvents.DisableNotifications -> {
                _sheetUIState.value = _sheetUIState.value.copy(
                    isNotificationEnabled = false,
                )
            }


            is CalenderUiEvents.OnNotificationOptionSelected -> {
                _sheetUIState.value = _sheetUIState.value.copy(
                    selectedNotificationOption = event.option,
                )
            }


            is CalenderUiEvents.OnDateSelected -> {
                val zoneId = ZoneId.systemDefault()


                event.date?.let { selectedDate ->
                    when (event.startOrEnd) {
                        0 -> {

                            if (Instant.ofEpochMilli(selectedDate)
                                    .atZone(zoneId)
                                    .toLocalDate().isAfter(
                                        Instant.ofEpochMilli(_sheetUIState.value.startDate)
                                            .atZone(zoneId)
                                            .toLocalDate()
                                    )
                            ) {

                                _sheetUIState.value = _sheetUIState.value.copy(
                                    startDate = selectedDate,
                                    endDate = selectedDate
                                )
                            } else {
                                _sheetUIState.value =
                                    _sheetUIState.value.copy(startDate = selectedDate)
                            }

                        }

                        1 -> {

                            if (Instant.ofEpochMilli(selectedDate)
                                    .atZone(zoneId)
                                    .toLocalDate().isBefore(
                                        Instant.ofEpochMilli(_sheetUIState.value.startDate)
                                            .atZone(zoneId)
                                            .toLocalDate()
                                    )
                            ) {
                                // If new end date is before start date, update both to maintain validity
                                _sheetUIState.value = _sheetUIState.value.copy(
                                    startDate = selectedDate,
                                    endDate = selectedDate
                                )
                            } else {
                                _sheetUIState.value =
                                    _sheetUIState.value.copy(endDate = selectedDate)
                            }

                        }
                    }
                }


            }

            is CalenderUiEvents.OnTImeSelected -> {
                val parts = event.time.split(":")
                val hour = parts[0].padStart(2, '0')
                val minute = parts[1].padStart(2, '0')
                val formattedTime = "$hour:$minute"

                val zoneId = ZoneId.systemDefault()

                when (event.startOrEnd) {

                    0 -> {
                        val startLocalDate = Instant.ofEpochMilli(_sheetUIState.value.startDate)
                            .atZone(zoneId)
                            .toLocalDate()
                        val endLocalDate = Instant.ofEpochMilli(_sheetUIState.value.endDate)
                            .atZone(zoneId)
                            .toLocalDate()
                        val newStartTime = LocalTime.parse(formattedTime)
                        val currentEndTime = LocalTime.parse(_sheetUIState.value.endTime)
                        if (startLocalDate == endLocalDate && newStartTime.isAfter(
                                currentEndTime
                            )
                        ) {
                            val newEndDate = startLocalDate
                                .atStartOfDay(zoneId)
                                .toInstant()
                                .toEpochMilli()
                            _sheetUIState.value = _sheetUIState.value.copy(
                                startTime = formattedTime,
                                endTime = formattedTime,
                                endDate = newEndDate
                            )

                        } else {
                            _sheetUIState.value =
                                _sheetUIState.value.copy(startTime = formattedTime)

                        }

                    }

                    1 -> { // End time selected

                        val startLocalDate = Instant.ofEpochMilli(_sheetUIState.value.startDate)
                            .atZone(zoneId)
                            .toLocalDate()
                        val endLocalDate = Instant.ofEpochMilli(_sheetUIState.value.endDate)
                            .atZone(zoneId)
                            .toLocalDate()

                        val newEndTime = LocalTime.parse(formattedTime)
                        val currentStartTime = LocalTime.parse(_sheetUIState.value.startTime)

                        if (startLocalDate == endLocalDate && newEndTime.isBefore(
                                currentStartTime
                            )
                        ) {
                            val newEndDate = endLocalDate.plusDays(1)
                                .atStartOfDay(zoneId)
                                .toInstant()
                                .toEpochMilli()

                            _sheetUIState.value = _sheetUIState.value.copy(
                                endTime = formattedTime,
                                endDate = newEndDate
                            )
                        } else {
                            _sheetUIState.value = _sheetUIState.value.copy(endTime = formattedTime)
                        }

                    }
                }

            }

            is CalenderUiEvents.OnItemSelected -> {
                _sheetUIState.value = SheetState(
                    isSheetVisible = true,
                    type = event.type
                )

            }


            is CalenderUiEvents.OnTitleChange -> {
                _sheetUIState.value = _sheetUIState.value.copy(
                    title = event.title
                )
            }


            is CalenderUiEvents.OnDescChange -> {
                _sheetUIState.value = _sheetUIState.value.copy(
                    description = event.desc
                )


            }

            is CalenderUiEvents.IsAllDayToggle -> {
                _sheetUIState.value = _sheetUIState.value.copy(
                    isAllDay = event.isAllDay
                )

            }


            is CalenderUiEvents.Refresh -> {
                viewModelScope.launch {
                    _state.update {
                        it.copy(loading = true)
                    }
                    delay(2000L)
                    getHolidays()
                    getAllEvents()
                }

            }

            is CalenderUiEvents.OnYearChanged -> {

                currentHolidayJob?.cancel()
                currentHolidayJob = viewModelScope.launch {
                    val monthOffset = event.page - Constants.INITIAL_PAGE
                    val year = LocalDate.now().plusMonths(monthOffset.toLong()).year
                    getHolidays(
                        year = year,
                        fetchFromRemote = true
                    )
                }

            }

            is CalenderUiEvents.OnSaveButtonClicked -> {
                addData(event.update)
                _sheetUIState.value = SheetState(
                    type = _sheetUIState.value.type
                )
            }

            is CalenderUiEvents.SetNotificationId -> {
                _sheetUIState.value = _sheetUIState.value.copy(
                    notificationId = event.id
                )
            }

            is CalenderUiEvents.AdjustDateForDateView -> {
                _sheetUIState.value = _sheetUIState.value.copy(
                    startDate = event.date.atStartOfDay(ZoneId.systemDefault()) // Convert to start of day in the system's default time zone
                        .toInstant() // Convert to Instant
                        .toEpochMilli(),
                    endDate = event.date.atStartOfDay(ZoneId.systemDefault()) // Convert to start of day in the system's default time zone
                        .toInstant() // Convert to Instant
                        .toEpochMilli()
                )
            }

            is CalenderUiEvents.UpdateDateForDateView -> {
                _state.update {
                    it.copy(
                        date = event.date
                    )
                }
            }


            is CalenderUiEvents.UpdateSheetState -> {
                savedStateHandle["itemParcel"] = event.item
                val item = savedStateHandle.get<EventParcel>("itemParcel")!!
                _sheetUIState.value = item.toEvent().toSheetState()

                println("SheetState : ${_sheetUIState.value}")
            }

            is CalenderUiEvents.DeleteEvent -> {
                deleteData()
                _sheetUIState.value = SheetState()
            }

            else -> Unit
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addData(update: Boolean) {
        viewModelScope.launch {

            _state.update {
                it.copy(
                    loading = true
                )
            }
            when (val result = eventRepository.addEvent(_sheetUIState.value.toEventDto())) {
                is Resource.Success -> {
                    getAllEvents()
                    _state.update {
                        it.copy(
                            loading = true
                        )
                    }
                    viewModelScope.launch {
                        if (update) {
                            snackBarState
                                .showSnackbar(
                                    message = "${_sheetUIState.value.type.name} updated successfully",
                                    duration = SnackbarDuration.Short
                                )
                        } else {
                            snackBarState
                                .showSnackbar(
                                    message = "${_sheetUIState.value.type.name} updated successfully",
                                    duration = SnackbarDuration.Short
                                )

                        }
                        _state.update {
                            it.copy(
                                loading = false
                            )
                        }
                    }


                }

                is Resource.Loading -> {
                    _state.update {
                        it.copy(
                            loading = true
                        )
                    }
                }

                else -> {
                    _state.update {
                        it.copy(
                            loading = false
                        )
                    }

                }
            }


        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun deleteData() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    loading = true
                )
            }
            when (val result = eventRepository.deleteEvent(_sheetUIState.value.toEventDto())) {
                is Resource.Success -> {
                    getAllEvents()
                    _state.update {
                        it.copy(
                            loading = true
                        )
                    }
                    viewModelScope.launch {
                        snackBarState
                            .showSnackbar(
                                message = "${_sheetUIState.value.type.name} deleted successfully",
                                duration = SnackbarDuration.Short
                            )
                        _state.update {
                            it.copy(
                                loading = false
                            )
                        }
                    }


                }

                is Resource.Loading -> {
                    _state.update {
                        it.copy(
                            loading = true
                        )
                    }
                }

                else -> {
                    _state.update {
                        it.copy(
                            loading = false
                        )
                    }

                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAllEvents(
        type: String = "ITEMS"
    ) {
        viewModelScope.launch {
            eventRepository.getAllEvent(type).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { list ->
                            _state.update {
                                it.copy(
                                    events = list.map { eventDto ->
                                        eventDto.toEvent()
                                    },
                                    loading = false,
                                )
                            }
                        }

                    }

                    is Resource.Loading -> {
                        _state.update {
                            it.copy(
                                loading = result.isLoading
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                error = result.message,
                                loading = false
                            )
                        }
                    }
                }

            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadEventsForMonth(monthOffset: Int, type: String = "ITEMS") {
        viewModelScope.launch {
            eventRepository.getEventsForMonth(monthOffset, type).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { list ->
                            _state.update {
                                it.copy(
                                    events = list.map { eventDto ->
                                        eventDto.toEvent()
                                    },
                                    loading = false,
                                )
                            }
                        }

                    }

                    is Resource.Loading -> {
                        _state.update {
                            it.copy(
                                loading = result.isLoading
                            )
                        }
                    }

                    is Resource.Error -> {
                        _state.update {
                            it.copy(
                                error = result.message,
                                loading = false
                            )
                        }
                    }
                }

            }
        }
    }

    private fun getHolidays(
        country: String = "IN",
        year: Int = LocalDate.now().year,
        query: String = state.value.query.lowercase(),
        fetchFromRemote: Boolean = false
    ) {
        viewModelScope.launch {
            holidayRepository
                .getHolidaysQuery(query, country, year, fetchFromRemote)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { listings ->
                                _state.update {
                                    it.copy(
                                        holidays = listings.map { holidayEntity ->
                                            holidayEntity.toHoliday()
                                        },
                                        loading = false

                                    )
                                }
                            }

                        }

                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    error = result.message,
                                    loading = false
                                )
                            }
                        }

                        is Resource.Loading -> {
                            _state.update {
                                it.copy(
                                    loading = result.isLoading
                                )
                            }
                        }
                    }
                }
        }


    }
}