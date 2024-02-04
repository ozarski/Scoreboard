package com.example.scoreboard.popups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scoreboard.ui.theme.onPrimaryContainerDark
import com.example.scoreboard.ui.theme.onPrimaryDark
import com.example.scoreboard.ui.theme.onSecondaryDark
import com.example.scoreboard.ui.theme.onTertiaryDark
import com.example.scoreboard.ui.theme.primaryContainerDark
import com.example.scoreboard.ui.theme.primaryDark
import com.example.scoreboard.ui.theme.secondaryDark
import com.example.scoreboard.ui.theme.tertiaryDark

class GenericPopupContent {

    companion object {
        @Composable
        fun GenerateContent(
            widthMin: Int? = null,
            widthMax: Int? = null,
            width: Int? = null,
            heightMin: Int? = null,
            heightMax: Int? = null,
            height: Int? = null,
            horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
            verticalArrangement: Arrangement.Vertical = Arrangement.SpaceEvenly,
            content: @Composable ColumnScope.() -> Unit
        ) {
            val minHeight = if (heightMin == null && height == null) 0 else heightMin ?: height!!
            val minWidth = if (widthMin == null && width == null) 0 else widthMin ?: width!!
            val maxHeight = if (heightMax == null && height == null) 0 else heightMax ?: height!!
            val maxWidth = if (widthMax == null && width == null) 0 else widthMax ?: width!!

            Column(
                modifier = Modifier
                    .widthIn(
                        min = minWidth.dp,
                        max = if (maxWidth == 0) Int.MAX_VALUE.dp else maxWidth.dp
                    )
                    .heightIn(
                        min = minHeight.dp,
                        max = if (maxHeight == 0) Int.MAX_VALUE.dp else maxHeight.dp
                    )
                    .background(primaryDark, RoundedCornerShape(25.dp))
                    .border(
                        width = 2.dp,
                        color = onPrimaryDark,
                        shape = RoundedCornerShape(25.dp)
                    ),
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = verticalArrangement
            ) {
                content()
            }
        }
    }
}

