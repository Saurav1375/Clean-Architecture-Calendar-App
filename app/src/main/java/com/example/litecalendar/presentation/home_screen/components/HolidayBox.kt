package com.example.litecalendar.presentation.home_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.litecalendar.domain.model.Holiday

@Composable
fun HolidayBox(
    holiday: Holiday,
    width : Dp = 50.dp,
    height : Dp = 15.dp,
    corner : Dp = 3.dp,
    textPadding : Dp = 0.dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(width, height)
            .background(
                MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(corner)
            )
        ,
        contentAlignment = Alignment.Center

    ) {
        Text(
            modifier = Modifier.fillMaxWidth().padding(start = textPadding),
            text = holiday.name,
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha =  0.9f)
        )
    }
}