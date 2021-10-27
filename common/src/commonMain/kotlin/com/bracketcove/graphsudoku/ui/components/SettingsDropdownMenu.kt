package com.bracketcove.graphsudoku.ui.components

import androidx.compose.runtime.Composable
import com.bracketcove.graphsudoku.ui.newgame.NewGameEvent

@Composable
expect fun SettingsDropdownMenu(
    onEventHandler: (NewGameEvent) -> Unit,
    titleText: String,
    initialIndex: Int,
    items: List<Any>
)