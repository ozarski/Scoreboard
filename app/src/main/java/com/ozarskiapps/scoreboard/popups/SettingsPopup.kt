package com.ozarskiapps.scoreboard.popups

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.database.ScoreboardDatabase
import com.ozarskiapps.scoreboard.MainActivity
import com.ozarskiapps.scoreboard.ui.theme.Typography
import com.ozarskiapps.scoreboard.ui.theme.onPrimaryDark
import com.ozarskiapps.scoreboard.ui.theme.primaryDark

class SettingsPopup(val context: Context, val activityContext: Activity) {

    private lateinit var popupVisible: MutableState<Boolean>

    @Composable
    fun GeneratePopup(popupVisible: MutableState<Boolean>) {
        this.popupVisible = popupVisible

        Popup(
            popupPositionProvider = WindowCenterOffsetPositionProvider(),
            onDismissRequest = {
                popupVisible.value = false
            },
            properties = PopupProperties(focusable = true)
        ) {
            GenericPopupContent.GenerateContent(
                verticalArrangement = Arrangement.Center
            ) {
                DataImportButton()
                DataExportButton()
            }
        }
    }

    @Composable
    fun DataImportButton() {
        Button(
            onClick = {
                pickFile()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = onPrimaryDark
            ),
            modifier = Modifier.padding(20.dp)
        ) {
            Text("Import Data", color = primaryDark, style = Typography.titleLarge)
        }
    }

    @Composable
    fun DataExportButton() {
        Button(
            onClick = {
                ScoreboardDatabase(context).exportDatabase()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = onPrimaryDark
            ),
            modifier = Modifier.padding(20.dp)
        ) {
            Text("Export Data", color = primaryDark, style = Typography.titleLarge)
        }
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            //can't filter for .db files so filtering for binary files (which includes .db
            //files but exclude most image, video, audio, txt etc.)
            type = "application/octet-stream"
        }

        activityContext.startActivityForResult(intent, MainActivity.PICK_DB_REQUEST_CODE)
    }
}