package com.ozarskiapps.scoreboard.popups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ozarskiapps.scoreboard.ui.theme.onPrimaryDark
import com.ozarskiapps.scoreboard.ui.theme.primaryDark

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
            val maxHeight = if (heightMax == null && height == null) Int.MAX_VALUE else heightMax ?: height!!
            val maxWidth = if (widthMax == null && width == null) Int.MAX_VALUE else widthMax ?: width!!

            Column(
                modifier = Modifier
                    .widthIn(
                        min = minWidth.dp,
                        max = maxWidth.dp
                    )
                    .heightIn(
                        min = minHeight.dp,
                        max = maxHeight.dp
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

