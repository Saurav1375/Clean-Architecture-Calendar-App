package com.example.litecalendar.presentation.home_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.litecalendar.R
import com.example.litecalendar.data.auth.UserData
import com.example.litecalendar.presentation.home_screen.CalenderState
import com.example.litecalendar.presentation.home_screen.CalenderUiEvents
import com.example.litecalendar.presentation.home_screen.HomeScreenViewModel
import com.example.litecalendar.presentation.home_screen.Screen
import com.example.litecalendar.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    state: CalenderState,
    showMonthBar: MutableState<Boolean>,
    pagerState: PagerState,
    monthOffset: Int,
    isDateView: Boolean = false,
    user: UserData?,
    navController: NavHostController,
    viewModel: HomeScreenViewModel,
    onNavBarClicked: () -> Unit,

    ) {
    val auth = FirebaseAuth.getInstance()
    val currentMonth = LocalDate.now().plusMonths(monthOffset.toLong())
    val monthYear = currentMonth.format(
        DateTimeFormatter.ofPattern("MMMM yyyy")
    )
    val scope = rememberCoroutineScope()
    var logout by rememberSaveable {
        mutableStateOf(false)
    }


    if (logout) {
        AlertDialog(
            onDismissRequest = { logout = false },
            title = {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = "Are you sure you want to Log Out?",
                    style = MaterialTheme.typography.bodyLarge

                )
            },
            confirmButton = {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Button(onClick = { logout = false }) {
                        Text(text = "Cancel")
                    }
                    Button(onClick = {
                        logout = false
                        auth.signOut()
                        navController.navigate(Screen.AuthScreen.route)
                    }) {
                        Text(text = "Log Out")

                    }

                }

            },
        )
    }

    val rotationAngle by animateFloatAsState(
        targetValue = if (state.loading) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
        ), label = ""
    )

    androidx.compose.material3.TopAppBar(
        title = {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .clickable(
                            enabled = !isDateView
                        ) {
                            showMonthBar.value = !showMonthBar.value
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        MonthHeader(monthOffset = monthOffset, dateView = isDateView)
                        Spacer(modifier = Modifier.width(4.dp))
                        if (!isDateView) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        }

                    }

                }

                IconButton(onClick = { navController.navigate(Screen.SearchScreen.route) }) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)

                }
                IconButton(onClick = {
                    scope.launch {
                        pagerState.scrollToPage(Constants.INITIAL_PAGE)
                    }
                }) {
                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null)

                }
                IconButton(
                    modifier = Modifier
                        .rotate(
                            if (state.loading) rotationAngle else 0f
                        ),
                    onClick = {
                        viewModel.onEvent(CalenderUiEvents.Refresh)
//                        isRotating = false
                        // Stop rotation after 2 seconds

                    })
                {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null)

                }

                Box(modifier = Modifier
                    .padding(start = 4.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .clickable {
                        logout = true
                    }) {
                    if (user != null) {
                        AsyncImage(
                            model = user.profilePictureUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }

            }


        },
    )
}