package com.bracketcove.graphsudoku.ui.newgame

import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.bracketcove.graphsudoku.ui.textColorDark
import com.bracketcove.graphsudoku.ui.textColorLight

//Anything which is not local to a class probably makes sense to make top level

@Composable
fun AppToolbar(
    modifier: Modifier,
    title: String,
    icon: @Composable () -> Unit
    ) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme
            .colors
            .primary,
        contentColor = Color.White,
        title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6,
                    color = if (MaterialTheme.colors.isLight) textColorLight else textColorDark,
                    textAlign = TextAlign.Start,
                    maxLines = 1
                )
        },
        actions = {
            icon()
        }
    )
}