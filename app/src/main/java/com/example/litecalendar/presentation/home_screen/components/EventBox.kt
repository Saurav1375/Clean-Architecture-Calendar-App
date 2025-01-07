package com.example.litecalendar.presentation.home_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.litecalendar.domain.model.Event
import com.example.litecalendar.domain.model.toEventParcel
import com.example.litecalendar.presentation.home_screen.Screen
import java.time.LocalDate

@Composable
fun EventBox(
    navController: NavHostController,
    event: Event,
    width: Dp = 50.dp,
    height: Dp = 15.dp,
    corner: Dp = 3.dp,
    textPadding: Dp = 0.dp,
    enable: Boolean = false,
    maxLines: Int = 1,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width, height)
            .background(
                if (event.type == "EVENT") MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(corner)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = textPadding),
            text = event.title,
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            color = if (event.type == "EVENT") MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onError
        )
    }
}