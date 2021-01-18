package com.bracketcove.graphsudoku.ui.newgame

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bracketcove.graphsudoku.ui.*

private const val TITLE = "New Game"

@Composable
fun NewGameToolbar(
    clickHandler: (() -> Unit),
    modifier: Modifier
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        title = {
            Row {
                Text(
                    text = TITLE,
                    style = MaterialTheme.typography.h6,
                    color = if (MaterialTheme.colors.isLight) textColorLight else textColorDark,
                    textAlign = TextAlign.Start,
                    maxLines = 1
                )
            }

        },
        actions = {
            Icon(
                imageVector = Icons.Filled.Done,
                tint = if (MaterialTheme.colors.isLight) textColorLight else textColorDark,
                modifier = Modifier
                    .clickable(onClick = { clickHandler.invoke() })
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .preferredHeight(36.dp),

                )

        }
    )
}