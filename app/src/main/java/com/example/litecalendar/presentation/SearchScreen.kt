package com.example.litecalendar.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.litecalendar.domain.model.toEventParcel
import com.example.litecalendar.presentation.home_screen.CalenderUiEvents
import com.example.litecalendar.presentation.home_screen.HomeScreenViewModel
import com.example.litecalendar.presentation.home_screen.Screen
import com.example.litecalendar.presentation.home_screen.components.EventBox
import com.example.litecalendar.presentation.home_screen.components.HolidayBox
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: HomeScreenViewModel
) {
    val state by viewModel.state.collectAsState()
    val filter = viewModel.filter.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            OutlinedTextField(
                value = state.query,
                textStyle = TextStyle(
                    color = Color.White
                ),
                onValueChange = {
                    viewModel.onEvent(CalenderUiEvents.OnSearchQueryChange(it))
                },
                leadingIcon = {
                    IconButton(onClick = {

                        viewModel.onEvent(CalenderUiEvents.OnSearch)

                    }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = {
                    Text(text = "Search...", color = Color(200, 200, 200))
                },
                maxLines = 1,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.onEvent(CalenderUiEvents.OnSearch)

                    }
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        items(filter.events, key = { it.toString() }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = it.startDate.format(DateTimeFormatter.ofPattern("dd MMM")),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = it.startDate.format(DateTimeFormatter.ofPattern("yyyy")),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                }
                                Spacer(modifier = Modifier.width(22.dp))
                                EventBox(
                                    navController = navController,
                                    event = it,
                                    textPadding = 8.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
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

                        items(filter.holidays, key = { it.toString() }) {
                            val date = LocalDate.parse(it.date.iso.substringBefore("T"))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = date.format(DateTimeFormatter.ofPattern("dd MMM")),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = date.format(DateTimeFormatter.ofPattern("yyyy")),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                }
                                Spacer(modifier = Modifier.width(22.dp))
                                HolidayBox(
                                    holiday = it, textPadding = 8.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .clickable {
                                            val itemParcel = it
                                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                                "itemParcel",
                                                itemParcel
                                            )
                                            navController.navigate(Screen.MainItemViewScreen.route + "/$itemParcel")
                                        },
                                    corner = 9.dp,

                                    )
                            }


                        }
                    }


                }
            }
        }
    }

}