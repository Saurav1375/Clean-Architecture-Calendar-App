package com.example.litecalendar.presentation.home_screen.components.events_view


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.HolidayVillage
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.litecalendar.domain.model.Holiday
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("NewApi")
@Composable
fun HolidayDetail(event: Holiday, modifier: Modifier = Modifier) {


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
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(4.dp)
                        )
                )
                Spacer(modifier = Modifier.width(32.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier,
                        text = event.name,
                        maxLines = 1,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    val date = LocalDate.parse(event.date.iso.substringBefore("T")).format(
                        DateTimeFormatter.ofPattern(
                            "EEEE, d MMM yyyy",
                            Locale.ENGLISH
                        )
                    )
                    Text(
                        modifier = Modifier,
                        text = date,
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyLarge,
                    )


                }
            }
            if (event.type.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(imageVector = Icons.Outlined.Tag, contentDescription = null)
                    Spacer(modifier = Modifier.width(32.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        event.type.forEach { tag ->
                            TagsView(tag = tag)
                        }
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

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(imageVector = Icons.Outlined.HolidayVillage, contentDescription = null)
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    modifier = Modifier,
                    text = event.primaryType,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge,
                )

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    modifier = Modifier,
                    text = event.locations,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge,
                )

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(imageVector = Icons.Outlined.CalendarToday, contentDescription = null)
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    modifier = Modifier,
                    text = "Holidays in ${event.country.name}",
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge,
                )

            }

        }

    }

}