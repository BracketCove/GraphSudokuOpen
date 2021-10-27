package com.bracketcove.graphsudoku.ui.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.bracketcove.graphsudoku.ui.textColorDark
import com.bracketcove.graphsudoku.ui.textColorLight

@Composable
fun AppToolbar(
    title: String,
    icon: @Composable () -> Unit
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                color = if (MaterialTheme.colors.isLight) textColorLight
            else textColorDark,
                textAlign = TextAlign.Start,
                maxLines = 1
            )
        },
        actions = {
            icon()
        }
    )
}