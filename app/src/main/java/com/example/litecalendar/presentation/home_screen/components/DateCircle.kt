package com.example.litecalendar.presentation.home_screen.components
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.litecalendar.domain.model.Event
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateCircle(
    date: LocalDate,
    size : Dp = 20.dp,
    modifier: Modifier = Modifier
) {
    val isToday = date == LocalDate.now()
    Box(
        modifier = modifier
            .size(size)
            .background(
                if (isToday) MaterialTheme.colorScheme.primary
                else Color.Transparent,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = if(size == 20.dp) MaterialTheme.typography.bodySmall else MaterialTheme.typography.titleLarge,
            color = if (isToday)
                MaterialTheme.colorScheme.background
            else
                MaterialTheme.colorScheme.onBackground
        )
    }
}