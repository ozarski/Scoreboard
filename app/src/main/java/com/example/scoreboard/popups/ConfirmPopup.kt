package com.example.scoreboard.popups

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
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
        decision: MutableState<Boolean>,
        otherPopupVisible: MutableState<Boolean>? = null,
        hideOtherPopup: Boolean = true
    ) {
        this.popupVisible = popupVisible
        this.otherPopupVisible = otherPopupVisible
        this.hideOtherPopup = hideOtherPopup
        this.decision = decision
        Popup(
            popupPositionProvider = WindowCenterOffsetPositionProvider(),
            onDismissRequest = {
                closePopup(false)
            },
            properties = PopupProperties(focusable = true)
        ) {
            ConfirmPopupLayout()
        }
    }

    @Composable
    private fun ConfirmPopupLayout() {
        GenericPopupContent.GenerateContent(
            width = 300,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            PopupQuestion()
            DecisionButtons()
        }
    }

    @Composable
    private fun PopupQuestion() {
        Text(
            text = stringResource(R.string.are_you_sure),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 20.dp)
                .fillMaxWidth()
        )
    }

    @Composable
    private fun DecisionButtons() {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DecisionButton(
                text = context.getString(R.string.yes_button_text),
                color = Color(context.getColor(R.color.delete_red)),
                decision = true
            )
            DecisionButton(
                text = context.getString(R.string.no_button_text),
                color = Color(context.getColor(R.color.main_ui_buttons_color)),
                decision = false
            )
        }
    }

    @Composable
    fun DecisionButton(text: String, color: Color, decision: Boolean) {
        Button(
            onClick = {
                closePopup(decision)
            },
            modifier = Modifier
            .padding(horizontal = 10.dp)
            .widthIn(100.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = color),
            elevation = ButtonDefaults.elevation(0.dp)
        ) {
            Text(text = text, color = Color.White)
        }
    }

    private fun closePopup(decisionValue: Boolean) {
        decision.value = decisionValue
        popupVisible.value = false
        if (otherPopupVisible != null) {
            otherPopupVisible!!.value = hideOtherPopup
        }
    }
}