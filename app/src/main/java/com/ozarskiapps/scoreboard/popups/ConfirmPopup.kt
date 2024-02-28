package com.ozarskiapps.scoreboard.popups

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import com.ozarskiapps.scoreboard.R
import com.ozarskiapps.scoreboard.ui.theme.Typography
import com.ozarskiapps.scoreboard.ui.theme.errorDark
import com.ozarskiapps.scoreboard.ui.theme.onErrorDark
import com.ozarskiapps.scoreboard.ui.theme.onPrimaryDark
import com.ozarskiapps.scoreboard.ui.theme.primaryDark

class ConfirmPopup(private val context: Context) {

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
                decision.value = false
                closePopup()
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
                .fillMaxWidth(),
            style = Typography.titleLarge,
            color = onPrimaryDark
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
                buttonColor = errorDark,
                textColor = onErrorDark,
                decision = true
            )
            DecisionButton(
                text = context.getString(R.string.no_button_text),
                buttonColor = onPrimaryDark,
                textColor = primaryDark,
                decision = false
            )
        }
    }

    @Composable
    fun DecisionButton(text: String, buttonColor: Color, textColor: Color, decision: Boolean) {
        Button(
            onClick = {
                this.decision.value = decision
                closePopup()
            },
            modifier = Modifier
            .padding(horizontal = 10.dp)
            .widthIn(100.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(text = text, color = textColor, style = Typography.titleLarge)
        }
    }

    private fun closePopup() {
        popupVisible.value = false
        if (otherPopupVisible != null) {
            otherPopupVisible!!.value = hideOtherPopup
        }
    }
}