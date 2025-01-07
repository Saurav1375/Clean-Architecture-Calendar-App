package com.example.litecalendar.presentation.home_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.litecalendar.utils.Constants
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MonthBar(pagerState: PagerState) {
    val scope = rememberCoroutineScope()

    // Pre-compute the month list using remember
    val barList = remember {
        buildList {
            (0..Constants.PAGE_COUNT).forEach {
                val offset = it - Constants.INITIAL_PAGE
                val month = LocalDate.now().plusMonths(offset.toLong())
                if (offset % 12 == 0) {
                    add(month.year.toString())
                }
                add(month.toString())
            }
        }
    }

    // Use derivedStateOf for computed values that depend on state
    val currentMonthIndex = remember(pagerState.currentPage) {
        derivedStateOf {
            val monthOffset = pagerState.currentPage - Constants.INITIAL_PAGE
            val currentDate = LocalDate.now().plusMonths(monthOffset.toLong())

            barList.indexOfFirst { dateStr ->
                if (!dateStr.matches(Regex("^\\d{4}$"))) {
                    val itemDate = LocalDate.parse(dateStr)
                    itemDate.month == currentDate.month && itemDate.year == currentDate.year
                } else false
            }
        }
    }

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = currentMonthIndex.value
    )

    // Optimize scroll behavior
    LaunchedEffect(pagerState.currentPage) {
        val targetIndex = currentMonthIndex.value
        if (targetIndex >= 0) {
            if (abs(listState.firstVisibleItemIndex - targetIndex) <= 10) {
                listState.animateScrollToItem(targetIndex)
            } else {
                listState.scrollToItem(targetIndex)
            }
        }
    }

    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(
            items = barList,
            key = { it } // Simplified key function
        ) { monthStr ->
            MonthBarBox(
                text = monthStr,
                isSelected = remember(pagerState.currentPage) {
                    derivedStateOf {
                        if (!monthStr.matches(Regex("^\\d{4}$"))) {
                            val monthOffset = pagerState.currentPage - Constants.INITIAL_PAGE
                            val currentDate = LocalDate.now().plusMonths(monthOffset.toLong())
                            val itemDate = LocalDate.parse(monthStr)
                            itemDate.month == currentDate.month && itemDate.year == currentDate.year
                        } else false
                    }
                }.value,
                onClick = {
                    if (!monthStr.matches(Regex("^\\d{4}$"))) {
                        val selectedDate = LocalDate.parse(monthStr)
                        val monthOffset = pagerState.currentPage - Constants.INITIAL_PAGE
                        val currentDate = LocalDate.now().plusMonths(monthOffset.toLong())
                        val yearOffset = selectedDate.year - currentDate.year
                        val offset = selectedDate.monthValue - currentDate.monthValue + yearOffset * 12

                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + offset)
                        }
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthBarBox(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val formatter = remember { DateTimeFormatter.ofPattern("MMM") }

    val displayText = remember(text) {
        if (text.matches(Regex("^\\d{4}$"))) {
            text
        } else {
            LocalDate.parse(text).format(formatter)
        }
    }

    val isYear = text.matches(Regex("^\\d{4}$"))

    Box(
        modifier = Modifier
            .size(57.dp, 33.dp)
            .clickable(
                enabled = !isYear,
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(2.dp)
            .background(
                if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                RoundedCornerShape(5.dp)
            )
            .border(
                BorderStroke(
                    width = if (!isYear) 0.4.dp else 0.dp,
                    color = if (!isYear) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                ),
                RoundedCornerShape(5.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
    }
}