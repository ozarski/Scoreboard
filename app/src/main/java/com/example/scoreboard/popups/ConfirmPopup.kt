package com.example.scoreboard.popups

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.scoreboard.R

class ConfirmPopup(val context: Context) {

    private lateinit var popupVisible: MutableState<Boolean>
    private var otherPopupVisible: MutableState<Boolean>? = null
    private var hideOtherPopup: Boolean = false
    private lateinit var decision: MutableState<Boolean>

    @Composable
    fun GeneratePopup(
        popupVisible: MutableState<Boolean>,
        decision : MutableState<Boolean>,
        otherPopupVisible: MutableState<Boolean>? = null,
        hideOtherPopup: Boolean = true,
    ) {
        this.popupVisible = popupVisible
        this.otherPopupVisible = otherPopupVisible
        this.hideOtherPopup = hideOtherPopup
        this.decision = decision
        Popup(
            popupPositionProvider = WindowCenterOffsetPositionProvider(),
            onDismissRequest = {
                popupVisible.value = false
                if (otherPopupVisible != null) {
                    otherPopupVisible.value = hideOtherPopup
                }
            },
            properties = PopupProperties(focusable = true)
        ) {
            ConfirmPopupLayout()
        }
    }

    @Composable
    private fun ConfirmPopupLayout() {
        Column(
            modifier = Modifier
                .width(300.dp)
                .background(Color.LightGray, RoundedCornerShape(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ){
            PopupHeader()
            YesNoButtons()
        }
    }

    @Composable
    private fun PopupHeader() {
        Text(
            text = "Are you sure",
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
        )
    }

    @Composable
    private fun YesNoButtons(){
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            val buttonsModifier = Modifier
                .padding(horizontal = 10.dp)
                .widthIn(100.dp)
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                YesButton(buttonsModifier)
                NoButton(buttonsModifier)
            }
        }
    }

    @Composable
    private fun YesButton(modifier: Modifier) {
        Button(
            onClick = {
                decision.value = true
                popupVisible.value = false
                if (otherPopupVisible != null) {
                    otherPopupVisible!!.value = hideOtherPopup
                }
            },
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            elevation = ButtonDefaults.elevation(0.dp)
        ) {
            Text(text = context.getString(R.string.yes_button_text), color = Color.White)
        }
    }

    @Composable
    private fun NoButton(modifier: Modifier) {
        Button(
            onClick = {
                decision.value = false
                popupVisible.value = false
                if (otherPopupVisible != null) {
                    otherPopupVisible!!.value = hideOtherPopup
                }
            },
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            elevation = ButtonDefaults.elevation(0.dp)
        ) {
            Text(text = context.getString(R.string.no_button_text))
        }
    }

}