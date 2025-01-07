package com.example.litecalendar.presentation.home_screen.components.day_view


import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.litecalendar.data.auth.UserData
import com.example.litecalendar.presentation.home_screen.CalenderUiEvents
import com.example.litecalendar.presentation.home_screen.HomeScreenViewModel
import com.example.litecalendar.presentation.home_screen.components.BottomModalSheet
import com.example.litecalendar.presentation.home_screen.components.FabItem
import com.example.litecalendar.presentation.home_screen.components.FabItemsView
import com.example.litecalendar.presentation.home_screen.components.RepeatMode
import com.example.litecalendar.presentation.home_screen.components.TopAppBar
import com.example.litecalendar.presentation.home_screen.components.ValueType
import com.example.litecalendar.utils.Constants
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayView(
    dateParam : String = LocalDate.now().toString(),
    navController : NavHostController,
    userData: UserData?,
    viewModel: HomeScreenViewModel = hiltViewModel(),
) {
    val currentDate = LocalDate.now()
    val daysDifference = ChronoUnit.DAYS.between(currentDate, LocalDate.parse(dateParam) ).toInt()
    val initialPage : Int = Constants.INITIAL_PAGE + daysDifference



    val pagerState = rememberPagerState(
        pageCount = { Constants.PAGE_COUNT * 31 },
        initialPage = initialPage
    )
    val state by viewModel.state.collectAsState()
    val sheetUiState by viewModel.sheetUIState
    val showMonthBar = rememberSaveable {
        mutableStateOf(false)
    }
    val fabItems = listOf(
        FabItem(
            name = "Event",
            icon = Icons.Default.Event,
            type = ValueType.EVENT
        ),
        FabItem(
            name = "Tasks",
            icon = Icons.Default.TaskAlt,
            type = ValueType.TASK
        )
    )

    var isFabExpanded by rememberSaveable { mutableStateOf(false) }

    // Observe and handle page changes
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                viewModel.onEvent(CalenderUiEvents.AdjustDateForDateView(LocalDate.parse(dateParam).plusDays((page - initialPage).toLong())))
                viewModel.onEvent(CalenderUiEvents.OnPageChanged(page = page))
            }
    }
    BottomModalSheet(isSheetVisible = sheetUiState.isSheetVisible, isDateView = true, date = LocalDate.parse(dateParam), viewModel = viewModel, user = userData)

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = viewModel.snackBarState,
            )

        },
        topBar = {
            TopAppBar(
                state = state,
                showMonthBar = showMonthBar,
                pagerState = pagerState,
                navController = navController,
                isDateView = true,
                monthOffset = state.page - Constants.INITIAL_PAGE,
                viewModel = viewModel,
                user = userData
            ) {
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {

            val expandTransition = updateTransition(
                targetState = isFabExpanded,
                label = "FAB Expand/Collapse"
            )

            // Background shape animation
            val fabWidth by expandTransition.animateDp(
                label = "FAB Width",
                transitionSpec = {
                    if (targetState) {
                        spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow)
                    } else {
                        spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMedium)
                    }
                }
            ) { expanded ->
                if (expanded) 200.dp else 56.dp
            }

            // Content alpha animation
            val contentAlpha by expandTransition.animateFloat(
                label = "Content Alpha",
                transitionSpec = {
                    if (targetState) {
                        tween(200, delayMillis = 100)
                    } else {
                        tween(100)
                    }
                }
            ) { expanded ->
                if (expanded) 1f else 0f
            }

            // Rotation animation for add icon
            val rotationAngle by expandTransition.animateFloat(
                label = "Rotation",
                transitionSpec = {
                    spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow)
                }
            ) { expanded ->
                if (expanded) 45f else 0f
            }

            FloatingActionButton(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .width(fabWidth),
                onClick = { isFabExpanded = !isFabExpanded },
                containerColor = MaterialTheme.colorScheme.surfaceBright,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier.padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!isFabExpanded) {
                        Icon(
                            modifier = Modifier
                                .rotate(rotationAngle)
                                .clickable { isFabExpanded = !isFabExpanded },
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add"
                        )
                    } else {
                        Row(
                            modifier = Modifier.alpha(contentAlpha),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            fabItems.forEach { item ->
                                FabItemsView(item = item) { fabItem ->
                                    viewModel.onEvent(
                                        CalenderUiEvents.OnSheetVisible(
                                            true,
                                            fabItem.type
                                        )
                                    )
                                }
                            }
                            IconButton(
                                onClick = { isFabExpanded = !isFabExpanded }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }


        }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            userScrollEnabled = true,
            beyondViewportPageCount = 2, // Preload 2 pages for smoother swiping
            pageSpacing = 0.dp,
            key = { key ->
                key
            } // Unique key for each page
        ) { page ->
            val dateOffset = remember(page) { page - Constants.INITIAL_PAGE }
            val date = LocalDate.now().plusDays(dateOffset.toLong())
            val currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

            DayViewGrid(
                pagerState = pagerState,
                state = state,
                date = date,
                events = state.events.filter {
                    when (it.repeatOption) {

                        RepeatMode.DOES_NOT_REPEAT.toString() -> it.startDate == date
                        RepeatMode.EVERYMONTH.toString() -> it.startDate.dayOfMonth == date.dayOfMonth
                        RepeatMode.EVERYDAY.toString() -> true
                        RepeatMode.EVERYWEEK.toString() -> it.startDate.dayOfWeek == date.dayOfWeek
                        RepeatMode.EVERYYEAR.toString() -> (it.startDate.dayOfMonth == date.dayOfMonth) && (it.startDate.monthValue == date.monthValue)
                        else -> false

                    }
                } , // Fast lookup for events
                holidays = state.holidays.filter { LocalDate.parse(it.date.iso.substringBefore("T")) == date },
                dateOffset = dateOffset,
                navController = navController
            )

        }
    }


}