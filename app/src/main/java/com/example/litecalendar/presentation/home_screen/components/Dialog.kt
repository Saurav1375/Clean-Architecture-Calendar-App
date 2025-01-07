package com.example.litecalendar.presentation.home_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.litecalendar.presentation.home_screen.CalenderUiEvents
import com.example.litecalendar.presentation.home_screen.HomeScreenViewModel

data class RepeatItem(
    val id: Int,
    val title: String,
    val repeatMode: RepeatMode
)

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ItemsDialog(
    modifier: Modifier = Modifier,
    sheetFields: SheetState,
    viewModel: HomeScreenViewModel,
    onDismissRequest: () -> Unit
) {
    val itemsList = listOf(
        RepeatItem(0, "Does not Repeat", RepeatMode.DOES_NOT_REPEAT),
        RepeatItem(1, "Every Day", RepeatMode.EVERYDAY),
        RepeatItem(2, "Every Week", RepeatMode.EVERYWEEK),
        RepeatItem(3, "Every Month", RepeatMode.EVERYMONTH),
        RepeatItem(4, "Every Year", RepeatMode.EVERYYEAR),

        )

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Column(
            modifier
                .selectableGroup()
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            itemsList.forEach { item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (item.repeatMode == sheetFields.selectedRadioOption),
                            onClick = {
                                viewModel.onEvent(CalenderUiEvents.OnRadioOptionSelected(item.repeatMode))
                                viewModel.onEvent(CalenderUiEvents.OnRepeatDialogVisible(false))
                            },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (item.repeatMode == sheetFields.selectedRadioOption),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }


    }

}
data class NotificationItem(
    val min: Int,
    val title: String,
    val before: Before
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationDialog(
    modifier: Modifier = Modifier,
    sheetFields: SheetState,
    viewModel: HomeScreenViewModel ,
    onDismissRequest: () -> Unit
) {
    val itemsList = listOf(
        NotificationItem(5, "5 minutes before", Before.FIVE),
        NotificationItem(10, "10 minutes before", Before.TEN),
        NotificationItem(15, "15 minutes before", Before.FIFTEEN),
        NotificationItem(30, "30 minutes before", Before.THIRTY),
        NotificationItem(60, "1 hour before", Before.ONE_HOUR),
        NotificationItem(60*24, "1 day before", Before.ONE_DAY),

        )

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Column(
            modifier
                .selectableGroup()
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            itemsList.forEach { item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (item.before == sheetFields.selectedNotificationOption),
                            onClick = {
                                viewModel.onEvent(CalenderUiEvents.OnNotificationOptionSelected(item.before))
                                viewModel.onEvent(CalenderUiEvents.OnNotificationDialogVisible(false))
                            },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (item.before == sheetFields.selectedNotificationOption),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }


    }

}