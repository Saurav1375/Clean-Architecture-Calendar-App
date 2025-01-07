package com.example.litecalendar.presentation.home_screen.components.events_view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.litecalendar.data.auth.UserData
import com.example.litecalendar.domain.model.Event
import com.example.litecalendar.presentation.home_screen.components.RepeatMode
import com.example.litecalendar.presentation.home_screen.components.getNotificationText
import com.example.litecalendar.presentation.home_screen.components.getRepeatText
import com.google.firebase.auth.FirebaseAuth
import java.time.format.DateTimeFormatter
import java.util.Locale

@SuppressLint("NewApi")
@Composable
fun EvenDetail(event: Event, modifier: Modifier = Modifier, user : UserData?) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .size(15.dp)
                        .background(
                            if (event.type == "EVENT") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                            RoundedCornerShape(4.dp)
                        )
                )
                Spacer(modifier = Modifier.width(32.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier,
                        text = event.title,
                        maxLines = 1,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    if (event.startDate == event.endDate || event.type != "EVENT") {
                        val date = event.startDate.format(
                            DateTimeFormatter.ofPattern(
                                "EEE, d MMM yyyy",
                                Locale.ENGLISH
                            )
                        )

                        if (event.type == "EVENT") {
                            if (!event.isAllDay) {
                                Text(
                                    modifier = Modifier,
                                    text = "$date  •  ${event.startTime} - ${event.endTime}",
                                    maxLines = 1,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            } else {
                                Text(
                                    modifier = Modifier,
                                    text = date,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }

                        } else {

                            if (!event.isAllDay) {
                                Text(
                                    modifier = Modifier,
                                    text = "$date  •  ${event.startTime}",
                                    maxLines = 1,
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            } else {
                                Text(
                                    modifier = Modifier,
                                    text = date,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.bodyLarge,
                                )

                            }

                        }


                    } else {
                        val startDate = event.startDate.format(
                            DateTimeFormatter.ofPattern(
                                "EEE, d MMM",
                                Locale.ENGLISH
                            )
                        )
                        val endDate = event.endDate.format(
                            DateTimeFormatter.ofPattern(
                                "EEE, d MMM",
                                Locale.ENGLISH
                            )
                        )
                        if (!event.isAllDay) {
                            Text(
                                modifier = Modifier,
                                text = "$startDate ${event.startTime}",
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                modifier = Modifier,
                                text = "$endDate ${event.endTime}",
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        } else {
                            Text(
                                modifier = Modifier,
                                text = startDate,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                modifier = Modifier,
                                text = endDate,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }


                    }

                    if (event.repeatOption != RepeatMode.DOES_NOT_REPEAT.name) {
                        Text(
                            modifier = Modifier,
                            text = getRepeatText(event.repeatOption),
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
            if (event.isNotificationEnabled || event.type == "TASK") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(imageVector = Icons.Outlined.Notifications, contentDescription = null)
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(
                        modifier = Modifier,
                        text = getNotificationText(event.alertTime),
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyLarge,
                    )


                }

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(imageVector = Icons.Outlined.CalendarToday, contentDescription = null)
                Spacer(modifier = Modifier.width(32.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        modifier = Modifier,
                        text = if (event.type == "EVENT") "Events" else "Tasks",
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                    )

                    user?.emailId?.let {
                        Text(
                            modifier = Modifier,
                            text = it,
                            maxLines = 1,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }


            }

            if (event.description.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(imageVector = Icons.Outlined.Description, contentDescription = null)
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(
                        modifier = Modifier,
                        text = event.description,
                        style = MaterialTheme.typography.bodyLarge,
                    )


                }
            }


        }

    }

}