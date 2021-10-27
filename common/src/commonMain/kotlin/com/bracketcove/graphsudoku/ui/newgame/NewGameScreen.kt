package com.bracketcove.graphsudoku.ui.newgame


import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bracketcove.graphsudoku.domain.DIFFICULTY
import com.bracketcove.graphsudoku.domain.UserRecords
import com.bracketcove.graphsudoku.toTime
import com.bracketcove.graphsudoku.ui.*
import com.bracketcove.graphsudoku.ui.components.AppToolbar
import com.bracketcove.graphsudoku.ui.components.LoadingScreen
import com.bracketcove.graphsudoku.ui.components.SettingsDropdownMenu

private const val TITLE = "New Game"

@Composable
fun NewGameScreen(
    onEventHandler: (NewGameEvent) -> Unit,
    viewModel: NewGameViewModel
) {
    val contentStateTransition = remember {
        MutableTransitionState(
            true
        )
    }

    val transition = updateTransition(contentStateTransition)

    val loadingAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) }
    ) {
        if (it) 1f else 0f
    }

    val mainAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) }
    ) {
        if (!it) 1f else 0f
    }

    viewModel.subLoadingState = {
        contentStateTransition.targetState = it
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
    ) {
        Box(Modifier.alpha(loadingAlpha)) { LoadingScreen() }
        if (!contentStateTransition.currentState) Box(Modifier.alpha(mainAlpha)) {
            NewGameContent(
                onEventHandler,
                viewModel
            )
        }
    }
}

@Composable
fun NewGameContent(
    onEventHandler: (NewGameEvent) -> Unit,
    viewModel: NewGameViewModel
) {
    Surface(
        Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Column (Modifier.background(MaterialTheme.colors.primary)) {
            AppToolbar(
                title = TITLE
            ) { DoneIcon(onEventHandler = onEventHandler) }

            Box(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 32.dp)
            ) {
                SettingsDropdownMenu(
                    onEventHandler,
                    STR_DIMENS,
                    when (viewModel.settingsState.boundary) {
                        4 -> 0
                        9 -> 1
                        else -> 0
                    },
                    listOf("4x4", "9x9")
                )
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                SettingsDropdownMenu(
                    onEventHandler,
                    STR_DIFFICULTY_TITLE,
                    viewModel.settingsState.difficulty.ordinal,
                    listOf(
                        DIFFICULTY.EASY,
                        DIFFICULTY.MEDIUM,
                        DIFFICULTY.HARD
                    )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 32.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                    .border(width = 2.dp, color = textColorLight, shape = RoundedCornerShape(8.dp))
                    .wrapContentHeight()
            ) {
                StatisticsView(viewModel.statisticsState)
            }
        }
    }
}


@Composable
fun StatisticsView(
    stats: UserRecords
) {
    val sizeFourStats = listOf(stats.fourEasy, stats.fourMedium, stats.fourHard)
    val sizeNineStats = listOf(stats.nineEasy, stats.nineMedium, stats.nineHard)

    Row(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        Column(Modifier.weight(1f)) {
            Text(
                text = "4x4",
                style = statsLabel.copy(textColorLight),
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                textAlign = TextAlign.Center
            )

            StatsColumn(
                stats = sizeFourStats,
                modifier = Modifier
                    .wrapContentSize()
            )
        }


        Spacer(
            Modifier
                .width(1.dp)
        )

        Column(Modifier.weight(1f)) {
            Text(
                text = "9x9",
                style = statsLabel.copy(color = textColorLight),
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                textAlign = TextAlign.Center
            )

            StatsColumn(
                stats = sizeNineStats,
                modifier = Modifier
                    .wrapContentSize()
            )
        }
    }
}

@Composable
fun DoneIcon(onEventHandler: (NewGameEvent) -> Unit) {
    Icon(
        imageVector = Icons.Filled.Done,
        tint = if (MaterialTheme.colors.isLight) textColorLight else textColorDark,
        contentDescription = null,
        modifier = Modifier
            .clickable(onClick = {
                onEventHandler.invoke(
                    NewGameEvent.OnDonePressed
                )
            })
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .height(36.dp),
    )
}

@Composable
fun StatsColumn(
    stats: List<Long>,
    modifier: Modifier
) {
    Column(
        modifier = modifier
    ) {
        stats.forEachIndexed { index, stat ->
            Row(
                Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Spacer(
                    Modifier.width(16.dp)
                )

                val isZero = (stat == 0L)

                Text(
                    text = stat.toTime(),
                    style = statsLabel.copy(
                        color = if (isZero) Color.White
                        else MaterialTheme.colors.onPrimary,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(end = 2.dp, bottom = 4.dp),
                    textAlign = TextAlign.End
                )

                (0..index).forEach {
                    Icon(
                        contentDescription = STR_DIFFICULTY,
                        imageVector = if (isZero) Icons.Outlined.StarBorder
                        else Icons.Filled.Star,
                        tint = if (isZero) Color.White
                        else MaterialTheme.colors.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(
            Modifier.height(16.dp)
        )
    }
}
