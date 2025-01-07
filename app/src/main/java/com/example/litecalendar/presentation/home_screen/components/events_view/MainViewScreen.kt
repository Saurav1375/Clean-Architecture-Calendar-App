package com.example.litecalendar.presentation.home_screen.components.events_view

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.work.WorkManager
import com.example.litecalendar.data.auth.UserData
import com.example.litecalendar.domain.model.Event
import com.example.litecalendar.domain.model.EventParcel
import com.example.litecalendar.domain.model.Holiday
import com.example.litecalendar.domain.model.toEvent
import com.example.litecalendar.presentation.home_screen.CalenderUiEvents
import com.example.litecalendar.presentation.home_screen.HomeScreenViewModel
import com.example.litecalendar.presentation.home_screen.components.BottomModalSheet
import com.example.litecalendar.presentation.home_screen.components.ValueType
import com.example.litecalendar.presentation.home_screen.components.toSheetState
import java.util.UUID

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainViewScreen(
    navController: NavHostController, item: Any, viewModel: HomeScreenViewModel, user: UserData?
) {
    var editEvent by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(true) {
        println(item)
    }

    BackHandler {
        navController.navigateUp()
        viewModel.onEvent(CalenderUiEvents.OnDiscard)
    }
    val context = LocalContext.current


    BottomModalSheet(
        isSheetVisible = viewModel.sheetUIState.value.isSheetVisible,
        viewModel = viewModel,
        updateSection = { navController.navigateUp() },
        update = true,
        user = user
    )

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                title = {
                    if (item is EventParcel) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    viewModel.onEvent(CalenderUiEvents.UpdateSheetState(item))
                                    viewModel.onEvent(
                                        CalenderUiEvents.OnSheetVisible(
                                            true,
                                            ValueType.valueOf(item.type)
                                        )
                                    )

                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "null"
                                    )

                                }
                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(onClick = {

                                    viewModel.onEvent(CalenderUiEvents.UpdateSheetState(item))
                                    if(item.type == "EVENT" && item.isNotificationEnabled && item.notificationId.isNotEmpty()){
                                        WorkManager.getInstance(context).cancelWorkById(UUID.fromString(item.notificationId))
                                    }
                                    navController.navigateUp()
                                    viewModel.onEvent(CalenderUiEvents.DeleteEvent)


                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "null"
                                    )

                                }

                            }

                        }
                    }

                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        when (item) {
            is EventParcel -> EvenDetail(
                event = item.toEvent(),
                modifier = Modifier.padding(paddingValues),
                user = user
            )

            is Holiday -> HolidayDetail(event = item, modifier = Modifier.padding(paddingValues))
            else -> {
                // Display error or empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No details available")
                }
            }
        }
    }
}
