package com.example.litecalendar.presentation.home_screen

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.litecalendar.data.auth.UserData
import com.example.litecalendar.presentation.MainActivity
import com.example.litecalendar.presentation.home_screen.components.BottomModalSheet
import com.example.litecalendar.presentation.home_screen.components.FabItem
import com.example.litecalendar.presentation.home_screen.components.FabItemsView
import com.example.litecalendar.presentation.home_screen.components.MonthBar
import com.example.litecalendar.presentation.home_screen.components.RepeatMode
import com.example.litecalendar.presentation.home_screen.components.TopAppBar
import com.example.litecalendar.presentation.home_screen.components.ValueType
import com.example.litecalendar.presentation.home_screen.components.day_view.DayView
import com.example.litecalendar.presentation.home_screen.components.day_view.DayViewGrid
import com.example.litecalendar.presentation.home_screen.components.month_view.CalendarGrid
import com.example.litecalendar.utils.Constants
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    user : UserData?,
    onDateClick: (String) -> Unit
) {
    val context = LocalContext.current as Activity

    BackHandler {
        context.finish()

    }

    val state by viewModel.state.collectAsState()
    val sheetUiState by viewModel.sheetUIState
    val pagerState = rememberPagerState(
        pageCount = { Constants.PAGE_COUNT },
        initialPage = Constants.INITIAL_PAGE
    )
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
                viewModel.onEvent(CalenderUiEvents.OnPageChanged(page = page))
            }
    }
    BottomModalSheet(isSheetVisible = sheetUiState.isSheetVisible, viewModel = viewModel, user = user)

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
                monthOffset = state.page - Constants.INITIAL_PAGE,
                viewModel = viewModel,
                user = user
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            if (showMonthBar.value) {
                MonthBar(pagerState = pagerState)
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize(),
                userScrollEnabled = true,
                beyondViewportPageCount = 2, // Preload 2 pages for smoother swiping
                pageSpacing = 0.dp,
                key = { key ->
                    key
                } // Unique key for each page
            ) { page ->
                val monthOffset = remember(page) { page - Constants.INITIAL_PAGE }
                val calendar =
                    remember(monthOffset) { CalendarUtils.initializeCalendar(monthOffset) }
                val dates = remember(calendar) { CalendarUtils.getVisibleDates(calendar) }

                CalendarGrid(
                    navController = navController ,
                    pagerState,
                    state = state,
                    dates = dates,
                    events = state.events.filter {
                        (it.startDate.monthValue == LocalDate.now()
                            .plusMonths(monthOffset.toLong()).monthValue &&
                                it.startDate.year == LocalDate.now()
                            .plusMonths(monthOffset.toLong()).year) || (it.repeatOption != RepeatMode.DOES_NOT_REPEAT.name)
                    },
                    holidays = state.holidays.filter {
                        it.date.datetime.month == LocalDate.now()
                            .plusMonths(monthOffset.toLong()).monthValue &&
                                it.date.datetime.year == LocalDate.now()
                            .plusMonths(monthOffset.toLong()).year
                    },
                    monthOffset = monthOffset,
                    onDateClick = { date ->
                        onDateClick(date.toString())
                    }
                )
            }
        }


    }


}
