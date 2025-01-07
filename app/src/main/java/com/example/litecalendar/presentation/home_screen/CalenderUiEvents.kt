package com.example.litecalendar.presentation.home_screen

import com.example.litecalendar.domain.model.Event
import com.example.litecalendar.domain.model.EventParcel
import com.example.litecalendar.presentation.home_screen.components.Before
import com.example.litecalendar.presentation.home_screen.components.RepeatMode
import com.example.litecalendar.presentation.home_screen.components.SheetState
import com.example.litecalendar.presentation.home_screen.components.ValueType
import java.time.LocalDate

sealed class CalenderUiEvents {
    data object Refresh : CalenderUiEvents()

    data object OnAddEvent : CalenderUiEvents()
    class OnPageChanged(val page: Int) : CalenderUiEvents()
    class OnYearChanged(val page : Int) : CalenderUiEvents()

    data class OnSheetVisible(val isSheetVisible : Boolean, val type: ValueType = ValueType.EVENT) : CalenderUiEvents()
    data class OnDatePickerVisible(val isDatePickerVisible : Boolean) : CalenderUiEvents()
    data class OnRepeatDialogVisible(val isDialogVisible : Boolean) : CalenderUiEvents()
    data class OnNotificationDialogVisible(val isDialogVisible : Boolean) : CalenderUiEvents()
    data class OnTimePickerVisible(val isTimePickerVisible : Boolean) : CalenderUiEvents()
    data class OnRadioOptionSelected(val option : RepeatMode) : CalenderUiEvents()
    data class OnNotificationOptionSelected(val option : Before) : CalenderUiEvents()
    data class OnDateSelected(val date : Long?, val startOrEnd : Int) : CalenderUiEvents()
    data class OnTImeSelected(val time : String, val startOrEnd : Int) : CalenderUiEvents()
    data class OnTitleChange(val title : String) : CalenderUiEvents()
    data class OnDescChange(val desc : String) : CalenderUiEvents()
    data class OnItemSelected(val type : ValueType) : CalenderUiEvents()
    data class IsAllDayToggle(val isAllDay : Boolean) : CalenderUiEvents()
    data object OnDiscard : CalenderUiEvents()
    data object DisableNotifications : CalenderUiEvents()
    data class AdjustDateForDateView(val date : LocalDate) : CalenderUiEvents()
    data class UpdateDateForDateView(val date : LocalDate) : CalenderUiEvents()
    data object DeleteEvent : CalenderUiEvents()
    data class SetNotificationId(val id : String) : CalenderUiEvents()
    data object OnSearch : CalenderUiEvents()

    data class UpdateEvent(val event : EventParcel) : CalenderUiEvents()
    data class UpdateSheetState(val item : EventParcel) : CalenderUiEvents()
    data class OnSearchQueryChange(val query : String) : CalenderUiEvents()

    data class OnSaveButtonClicked( val update : Boolean) : CalenderUiEvents()
}