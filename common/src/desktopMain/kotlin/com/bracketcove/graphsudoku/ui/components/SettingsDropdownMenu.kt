package com.bracketcove.graphsudoku.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bracketcove.graphsudoku.domain.DIFFICULTY
import com.bracketcove.graphsudoku.toLocalizedResource
import com.bracketcove.graphsudoku.ui.STR_DIFFICULTY
import com.bracketcove.graphsudoku.ui.STR_DROPDOWN_ARROW
import com.bracketcove.graphsudoku.ui.dropdownText
import com.bracketcove.graphsudoku.ui.newGameSubtitle
import com.bracketcove.graphsudoku.ui.newgame.NewGameEvent

@Composable
actual fun SettingsDropdownMenu(
    onEventHandler: (NewGameEvent) -> Unit,
    titleText: String,
    initialIndex: Int,
    items: List<Any>
) {
    //I wanted to reduce repetitive code, but the two menus are rendered slightly differently
    val isSizeMenu = items[0] is String

    var showMenu by remember { mutableStateOf(false) }
    var menuIndex by remember {
        mutableStateOf(
            initialIndex
        )
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = titleText,
            style = newGameSubtitle.copy(color = MaterialTheme.colors.secondary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp),
        )

        Row(
            Modifier.clickable(
                onClick = { showMenu = true }
            )
        ) {
            Text(
                text = if (isSizeMenu) items[menuIndex] as String
                else (items[menuIndex] as DIFFICULTY).toLocalizedResource,
                style = newGameSubtitle.copy(
                    color = MaterialTheme.colors.onPrimary,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier
                    .wrapContentSize()
            )

            Icon(
                contentDescription = STR_DROPDOWN_ARROW,
                imageVector = Icons.Outlined.ArrowDropDown,
                tint = MaterialTheme.colors.secondary,
                modifier = Modifier
                    .size(48.dp)
                    .rotate(
                        animateFloatAsState(
                            if (!showMenu) 0f else 180f,
                        ).value
                    )
                    .align(Alignment.CenterVertically)
            )

            DropdownMenu(
                modifier = Modifier.background(MaterialTheme.colors.surface),
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
            ) {
                items.forEachIndexed { index, _ ->
                    DropdownMenuItem(
                        onClick = {
                            menuIndex = index
                            showMenu = false
                            onEventHandler.invoke(
                                //the first.toString.toInt is so we don't get the ASCII value
                                if (isSizeMenu) NewGameEvent.OnSizeChanged(
                                    (items[index] as String)
                                        .first().toString().toInt()
                                )
                                else NewGameEvent.OnDifficultyChanged((items[index] as DIFFICULTY))
                            )
                        },
                        modifier = Modifier
                            .wrapContentSize()
                            .background(MaterialTheme.colors.surface)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isSizeMenu) items[index] as String
                                else (items[index] as DIFFICULTY).toLocalizedResource,
                                style = dropdownText(MaterialTheme.colors.primaryVariant),
                                modifier = Modifier.padding(end = 8.dp)
                            )

                            if (!isSizeMenu) {
                                (0..index).forEach {
                                    Icon(
                                        contentDescription = STR_DIFFICULTY,
                                        imageVector = Icons.Filled.Star,
                                        tint = MaterialTheme.colors.primaryVariant,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}