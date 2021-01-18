package com.bracketcove.graphsudoku.ui.newgame

import androidx.compose.animation.animateAsState
import androidx.compose.animation.core.animateAsState
import androidx.compose.animation.transition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bracketcove.graphsudoku.R
import com.bracketcove.graphsudoku.common.toLocalizedResource
import com.bracketcove.graphsudoku.common.toTime
import com.bracketcove.graphsudoku.domain.Difficulty
import com.bracketcove.graphsudoku.domain.Settings
import com.bracketcove.graphsudoku.domain.UserStatistics
import com.bracketcove.graphsudoku.ui.*
import com.google.ads.AdSize


@Composable
fun NewGameScreen(
    onEventHandler: (NewGameEvent) -> Unit,
    viewModel: NewGameViewModel
) {
    var showLoading by remember { mutableStateOf(true) }

    viewModel.subLoadingState = { showLoading = it }

    val contentStateTransition = transition(
        newGameViewTransition(),
        showLoading
    )

    GraphSudokuTheme {
        Column {
            if (showLoading) Box(
                Modifier
                    .alpha(
                        contentStateTransition[loadingAlphaKey]
                    )
            ) {
                LoadingScreen()
            }
            else Box(
                Modifier
                    .alpha(
                        contentStateTransition[newGameAlphaKey]
                    )
            ) {
                NewGameContent(
                    onEventHandler,
                    viewModel
                )
            }

            Box(
                Modifier.fillMaxWidth()
                    .height(90.dp)
                    .background(MaterialTheme.colors.primary)
                    .padding(bottom = 8.dp)
            ) {
                BannerAd()
            }
        }
    }
}

@Composable
fun NewGameContent(
    onEventHandler: (NewGameEvent) -> Unit,
    viewModel: NewGameViewModel
) {
    Surface(Modifier.fillMaxSize()) {
        ConstraintLayout(Modifier.background(MaterialTheme.colors.primary)) {
            val (toolbar,
                sizeDropdown,
                diffDropdown,
                stats
            ) = createRefs()


            NewGameToolbar(
                clickHandler = {
                    onEventHandler.invoke(
                        NewGameEvent.OnDonePressed
                    )
                },
                Modifier.constrainAs(toolbar) {
                    top.linkTo(parent.top)
                }
            )

            Box(Modifier.fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 32.dp)
                .constrainAs(sizeDropdown) {
                    top.linkTo(toolbar.bottom)
                }) {
                DropdownWithTitle(
                    onEventHandler,
                    stringResource(R.string.dimensions),
                    when (viewModel.settingsState.boundary) {
                        4 -> 0
                        9 -> 1
                        else -> 0
                    },
                    listOf("4x4", "9x9")
                )
            }

            Box(Modifier.fillMaxWidth()
                .wrapContentHeight()
                .constrainAs(diffDropdown) {
                    top.linkTo(sizeDropdown.bottom)
                }) {
                DropdownWithTitle(
                    onEventHandler,
                    stringResource(R.string.difficulty_title),
                    viewModel.settingsState.difficulty.ordinal,
                    listOf(
                        Difficulty.EASY,
                        Difficulty.MEDIUM,
                        Difficulty.HARD
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
                    .constrainAs(stats) {
                        top.linkTo(diffDropdown.bottom)
                    },
            ) {
                StatisticsView(viewModel.statisticsState)
            }

        }
    }
}

@Composable
fun DropdownWithTitle(
    onEventHandler: (NewGameEvent) -> Unit,
    titleText: String,
    initialIndex: Int,
    items: List<Any>,
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
            modifier = Modifier.fillMaxWidth()
                .padding(start = 32.dp),
        )

        DropdownMenu(
            toggle = {
                Row(Modifier.clickable(onClick = { showMenu = true })) {

                    Text(
                        text = if (isSizeMenu) items[menuIndex] as String
                        else stringResource(id = (items[menuIndex] as Difficulty).toLocalizedResource),
                        style = newGameSubtitle.copy(
                            color = MaterialTheme.colors.onPrimary,
                            fontWeight = FontWeight.Normal
                        ),
                        modifier = Modifier
                            .wrapContentSize()
                    )

                    Icon(
                        imageVector = Icons.Outlined.ArrowDropDown
                            .copy(defaultHeight = 48.dp, defaultWidth = 48.dp),
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .rotate(
                                animateAsState(
                                    if (showMenu == false) 0f else 180f,
                                ).value
                            ).align(Alignment.CenterVertically)
                    )
                }
            },
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            toggleModifier = Modifier
                .wrapContentSize(),
            dropdownModifier = Modifier
                .wrapContentSize()
                .background(MaterialTheme.colors.surface),
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
                            else NewGameEvent.OnDifficultyChanged((items[index] as Difficulty))
                        )
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isSizeMenu) items[index] as String
                            else stringResource(id = (items[index] as Difficulty).toLocalizedResource),
                            style = dropdownText(MaterialTheme.colors.primaryVariant),
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        if (!isSizeMenu) {
                            (0..index).forEach {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    tint = MaterialTheme.colors.primaryVariant,
                                    modifier = Modifier.preferredSize(36.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun StatisticsView(
    stats: UserStatistics
) {
    val sizeFourStats = listOf(stats.fourEasy, stats.fourMedium, stats.fourHard)
    val sizeNineStats = listOf(stats.nineEasy, stats.nineMedium, stats.nineHard)

    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp),
    ) {

        val (fourTitle, fourColumn, divHor, nineTitle, nineColumn) = createRefs()

        Text(
            text = "4x4",
            style = statsLabel.copy(textColorLight),
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(fourTitle) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(divHor.start)
                }
                .padding(bottom = 4.dp)
        )

        StatsColumn(
            stats = sizeFourStats,
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(fourColumn) {
                    top.linkTo(fourTitle.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(divHor.start)
                }
        )

        Divider(
            Modifier
                .size(1.dp)
                .constrainAs(divHor) {
                    centerHorizontallyTo(parent)
                }
        )

        Text(
            text = "9x9",
            style = statsLabel.copy(color = textColorLight),
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(nineTitle) {
                    top.linkTo(parent.top)
                    start.linkTo(divHor.end)
                    end.linkTo(parent.end)
                }
                .padding(bottom = 4.dp),
        )

        StatsColumn(
            stats = sizeNineStats,
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(nineColumn) {
                    top.linkTo(nineTitle.bottom)
                    centerHorizontallyTo(nineTitle)
                }
        )
    }

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
                        imageVector = if (isZero) Icons.Outlined.StarBorder
                        else Icons.Filled.Star,
                        tint = if (isZero) Color.White
                        else MaterialTheme.colors.onPrimary,
                        modifier = Modifier.preferredSize(24.dp)
                    )
                }
            }
        }

        Spacer(
            Modifier.height(16.dp)
        )
    }
}

@Preview
@Composable
fun previewNewGameContent() {
    GraphSudokuTheme {
        NewGameContent(onEventHandler = { /*TODO*/ },
            viewModel = NewGameViewModel().apply {
                settingsState = Settings(
                    Difficulty.MEDIUM,
                    9
                )

                statisticsState = UserStatistics(
                    80,
                    0,
                    0,
                    0,
                    0,
                    0
                )
            }
        )
    }
}
