package com.ozarskiapps.scoreboard.popups

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.unit.dp
import com.ozarskiapps.scoreboard.ui.theme.Typography
import com.ozarskiapps.scoreboard.ui.theme.onPrimaryDark
import com.ozarskiapps.scoreboard.ui.theme.primaryDark
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.util.Calendar

class PickDateDialog(private val date: MutableState<Calendar>) {

    @ExperimentalMaterial3Api
    @Composable
    fun GenerateDialog(onDismiss: () -> Unit) {
        val dialogState = rememberMaterialDialogState()
        var pickedDate = LocalDate.of(
            date.value.get(Calendar.YEAR),
            date.value.get(Calendar.MONTH) + 1,
            date.value.get(Calendar.DAY_OF_MONTH)
        )

        MaterialDialog(
            dialogState = dialogState,
            buttons = {
                positiveButton("OK", textStyle = Typography.titleLarge) {
                    date.value.run {
                        set(Calendar.YEAR, pickedDate.year)
                        set(Calendar.MONTH, pickedDate.monthValue - 1)
                        set(Calendar.DAY_OF_MONTH, pickedDate.dayOfMonth)
                    }
                    onDismiss()
                    dialogState.hide()
                }
                negativeButton("Cancel", textStyle = Typography.titleLarge) {
                    onDismiss()
                    dialogState.hide()
                }
            },
            onCloseRequest = {
                onDismiss()
                dialogState.hide()
            },
            shape = RoundedCornerShape(16.dp)
        ) {
            datepicker(
                initialDate = pickedDate,
                colors = DatePickerDefaults.colors(
                    headerBackgroundColor = primaryDark,
                    headerTextColor = onPrimaryDark,
                    dateActiveBackgroundColor = primaryDark,
                    dateActiveTextColor = onPrimaryDark,
                    dateInactiveTextColor = onPrimaryDark
                ),
                title = "Select session date",
                allowedDateValidator = { date ->
                    date.isBefore(LocalDate.now().run {
                        atStartOfDay()
                        plusDays(1)
                    })
                }
            ) {
                pickedDate = it
            }
        }

        dialogState.show()
    }
}