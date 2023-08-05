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

class GenericPopupContent {

    companion object{
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
            if (widthMin == null || widthMax == null) {
                if (heightMin == null || heightMax == null) {
                    if (height == null && width == null) {
                        GenericPopupContent().ColumnNoHeightNoWidth(
                            content = content,
                            horizontalAlignment = horizontalAlignment,
                            verticalArrangement = verticalArrangement
                        )
                    } else if (height == null) {
                        GenericPopupContent().ColumnWidthNoHeight(
                            content = content,
                            width = width!!,
                            horizontalAlignment = horizontalAlignment,
                            verticalArrangement = verticalArrangement
                        )
                    } else if (width == null) {
                        GenericPopupContent().ColumnHeightNoWidth(
                            content = content,
                            height = height,
                            horizontalAlignment = horizontalAlignment,
                            verticalArrangement = verticalArrangement
                        )
                    } else {
                        GenericPopupContent().ColumnWidthHeight(
                            content = content,
                            width = width,
                            height = height,
                            horizontalAlignment = horizontalAlignment,
                            verticalArrangement = verticalArrangement
                        )
                    }
                } else {
                    if (width == null) {
                        GenericPopupContent().ColumnMinMaxHeightNoWidth(
                            content = content,
                            heightMin = heightMin,
                            heightMax = heightMax,
                            horizontalAlignment = horizontalAlignment,
                            verticalArrangement = verticalArrangement
                        )
                    } else {
                        GenericPopupContent().ColumnWidthMinMaxHeight(
                            content = content,
                            width = width,
                            heightMin = heightMin,
                            heightMax = heightMax,
                            horizontalAlignment = horizontalAlignment,
                            verticalArrangement = verticalArrangement
                        )
                    }
                }
            } else {
                if (heightMin == null || heightMax == null) {
                    if(height == null){
                        GenericPopupContent().ColumnMinMaxWidthNoHeight(
                            content = content,
                            widthMin = widthMin,
                            widthMax = widthMax,
                            horizontalAlignment = horizontalAlignment,
                            verticalArrangement = verticalArrangement
                        )
                    } else {
                        GenericPopupContent().ColumnMinMaxWidthHeight(
                            content = content,
                            widthMin = widthMin,
                            widthMax = widthMax,
                            height = height,
                            horizontalAlignment = horizontalAlignment,
                            verticalArrangement = verticalArrangement
                        )
                    }
                } else {
                    GenericPopupContent().ColumnMinMaxWidthMinMaxHeight(
                        content = content,
                        widthMin = widthMin,
                        widthMax = widthMax,
                        heightMin = heightMin,
                        heightMax = heightMax,
                        horizontalAlignment = horizontalAlignment,
                        verticalArrangement = verticalArrangement
                    )
                }
            }
        }
    }

    @Composable
    private fun ColumnWidthHeight(
        content: @Composable ColumnScope.() -> Unit,
        width: Int,
        height: Int,
        horizontalAlignment: Alignment.Horizontal,
        verticalArrangement: Arrangement.Vertical
    ) {

        Column(
            modifier = Modifier
                .width(width.dp)
                .height(height.dp)
                .background(Color.White, RoundedCornerShape(25.dp))
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(25.dp)
                ),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement
        ) {
            content()
        }
    }

    @Composable
    private fun ColumnWidthNoHeight(
        content: @Composable ColumnScope.() -> Unit,
        width: Int,
        horizontalAlignment: Alignment.Horizontal,
        verticalArrangement: Arrangement.Vertical
    ) {

        Column(
            modifier = Modifier
                .width(width.dp)
                .background(Color.White, RoundedCornerShape(25.dp))
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(25.dp)
                ),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement
        ) {
            content()
        }
    }

    @Composable
    private fun ColumnHeightNoWidth(
        content: @Composable ColumnScope.() -> Unit,
        height: Int,
        horizontalAlignment: Alignment.Horizontal,
        verticalArrangement: Arrangement.Vertical
    ) {
        Column(
            modifier = Modifier
                .height(height.dp)
                .background(Color.White, RoundedCornerShape(25.dp))
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(25.dp)
                ),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement
        ) {
            content()
        }
    }

    @Composable
    private fun ColumnNoHeightNoWidth(
        content: @Composable ColumnScope.() -> Unit,
        horizontalAlignment: Alignment.Horizontal,
        verticalArrangement: Arrangement.Vertical
    ) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(25.dp))
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(25.dp)
                ),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement
        ) {
            content()
        }
    }

    @Composable
    private fun ColumnMinMaxWidthNoHeight(
        content: @Composable ColumnScope.() -> Unit,
        widthMin: Int,
        widthMax: Int,
        horizontalAlignment: Alignment.Horizontal,
        verticalArrangement: Arrangement.Vertical
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = widthMin.dp, max = widthMax.dp)
                .background(Color.White, RoundedCornerShape(25.dp))
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(25.dp)
                ),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement
        ) {
            content()
        }
    }

    @Composable
    private fun ColumnMinMaxHeightNoWidth(
        content: @Composable ColumnScope.() -> Unit,
        heightMin: Int,
        heightMax: Int,
        horizontalAlignment: Alignment.Horizontal,
        verticalArrangement: Arrangement.Vertical
    ) {
        Column(
            modifier = Modifier
                .heightIn(min = heightMin.dp, max = heightMax.dp)
                .background(Color.White, RoundedCornerShape(25.dp))
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(25.dp)
                ),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement
        ) {
            content()
        }
    }

    @Composable
    private fun ColumnMinMaxWidthMinMaxHeight(
        content: @Composable ColumnScope.() -> Unit,
        widthMin: Int,
        widthMax: Int,
        heightMin: Int,
        heightMax: Int,
        horizontalAlignment: Alignment.Horizontal,
        verticalArrangement: Arrangement.Vertical
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = widthMin.dp, max = widthMax.dp)
                .heightIn(min = heightMin.dp, max = heightMax.dp)
                .background(Color.White, RoundedCornerShape(25.dp))
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(25.dp)
                ),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement
        ) {
            content()
        }
    }

    @Composable
    private fun ColumnWidthMinMaxHeight(
        content: @Composable ColumnScope.() -> Unit,
        width: Int,
        heightMin: Int,
        heightMax: Int,
        horizontalAlignment: Alignment.Horizontal,
        verticalArrangement: Arrangement.Vertical
    ) {
        Column(
            modifier = Modifier
                .width(width.dp)
                .heightIn(min = heightMin.dp, max = heightMax.dp)
                .background(Color.White, RoundedCornerShape(25.dp))
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(25.dp)
                ),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement
        ) {
            content()
        }
    }

    @Composable
    private fun ColumnMinMaxWidthHeight(
        content: @Composable ColumnScope.() -> Unit,
        widthMin: Int,
        widthMax: Int,
        height: Int,
        horizontalAlignment: Alignment.Horizontal,
        verticalArrangement: Arrangement.Vertical
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = widthMin.dp, max = widthMax.dp)
                .height(height.dp)
                .background(Color.White, RoundedCornerShape(25.dp))
                .border(
                    width = 2.dp,
                    color = Color.LightGray,
                    shape = RoundedCornerShape(25.dp)
                ),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement
        ) {
            content()
        }
    }
}

