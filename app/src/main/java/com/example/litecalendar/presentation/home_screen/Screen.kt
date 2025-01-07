package com.example.litecalendar.presentation.home_screen

sealed class Screen(
    val route: String
) {
    data object MonthViewScreen : Screen("monthviewscreen")
    data object  DayViewScreen: Screen("dayviewscreen")
    data object  MainItemViewScreen: Screen("mainitemviewscreen")
    data object  AuthScreen: Screen("authscreen")
    data object  SearchScreen: Screen("searchscreen")
}