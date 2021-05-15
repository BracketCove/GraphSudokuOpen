package com.bracketcove.graphsudoku.ui.activegame

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.bracketcove.graphsudoku.R
import com.bracketcove.graphsudoku.common.toTime
import com.bracketcove.graphsudoku.computationlogic.sqrt
import com.bracketcove.graphsudoku.ui.*
import com.bracketcove.graphsudoku.ui.newgame.AppToolbar

enum class ActiveGameScreenState {
    LOADING,
    ACTIVE,
    COMPLETE
}

private const val TITLE = "Graph Sudoku"

@Composable
fun ActiveGameScreen(
    onEventHandler: (ActiveGameEvent) -> Unit,
    viewModel: ActiveGameViewModel
) {
    val contentTransitionState = remember {
        MutableTransitionState(
            ActiveGameScreenState.LOADING
        )
    }

    val transition = updateTransition(contentTransitionState)

    val loadingAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) }
    ) {
        if (it == ActiveGameScreenState.LOADING) 1f else 0f
    }

    val activeAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) }
    ) {
        if (it == ActiveGameScreenState.ACTIVE) 1f else 0f
    }

    val completeAlpha by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) }
    ) {
        if (it == ActiveGameScreenState.COMPLETE) 1f else 0f
    }

    viewModel.subContentState = {
        contentTransitionState.targetState = it
    }

    Column(
        Modifier
            .background(MaterialTheme.colors.primary)
            .fillMaxHeight()
    ) {

        AppToolbar(
            modifier = Modifier
                .wrapContentHeight(),
            title = TITLE,
        ) {
            NewGameIcon(onEventHandler = onEventHandler)
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 4.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            when (contentTransitionState.currentState) {
                ActiveGameScreenState.ACTIVE -> Box(
                    Modifier
                        .alpha(activeAlpha)
                ) {
                    GameContent(
                        onEventHandler,
                        viewModel
                    )
                }
                ActiveGameScreenState.COMPLETE -> Box(
                    Modifier
                        .alpha(completeAlpha)
                ) {
                    GameCompleteContent(
                        viewModel.timerState,
                        viewModel.isNewRecordState
                    )
                }
                ActiveGameScreenState.LOADING -> Box(
                    Modifier
                        .alpha(loadingAlpha)
                ) {
                    LoadingScreen()
                }
            }
        }
    }
}


@Composable
fun GameContent(
    onEventHandler: (ActiveGameEvent) -> Unit,
    viewModel: ActiveGameViewModel
) {
    BoxWithConstraints {
        val screenWidth = with(LocalDensity.current) {
            constraints.maxWidth.toDp()
        }

        //Below a certain screen height, I need to make the sudoku board smaller
        val margin = with(LocalDensity.current) {
            when {
                constraints.maxHeight.toDp().value < 500f -> 20
                constraints.maxHeight.toDp().value < 550f -> 8
                else -> 0
            }
        }

        ConstraintLayout {
            val (board, timer, diff, inputs) = createRefs()

            var isComplete by remember {
                mutableStateOf(false)
            }

            viewModel.subIsCompleteState = { isComplete = it }

            //Add Puzzle board component
            Box(Modifier
                .constrainAs(board) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .background(MaterialTheme.colors.surface)
                .size(screenWidth - margin.dp)
                .border(
                    width = 2.dp, color = animateColorAsState(
                        if (isComplete) victoryColor
                        else MaterialTheme.colors.primaryVariant
                    ).value
                )
            ) {
                SudokuBoard(
                    onEventHandler,
                    viewModel,
                    screenWidth - margin.dp
                )
            }

            Box(Modifier
                .wrapContentSize()
                .constrainAs(timer) {
                    top.linkTo(board.bottom)
                    start.linkTo(parent.start)
                }
                .padding(start = 16.dp))
            {
                TimerText(viewModel)
            }

            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(diff) {
                        top.linkTo(board.bottom)
                        end.linkTo(parent.end)
                    },
            ) {
                (0..viewModel.difficulty.ordinal).forEach {
                    Icon(
                        contentDescription = stringResource(R.string.difficulty),
                        imageVector = Icons.Filled.Star,
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(top = 4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .constrainAs(inputs) {
                        top.linkTo(timer.bottom)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //TODO reimplement FlowRow now that it's deprecated :(
                if (viewModel.boundary == 4) {
                    Row {
                        (0..viewModel.boundary).forEach {
                            SudokuInputButton(
                                onEventHandler,
                                it
                            )
                        }
                    }

                } else {
                    Row {
                        (0..4).forEach {
                            SudokuInputButton(
                                onEventHandler,
                                it
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(2.dp))

                    Row {
                        (5..9).forEach {
                            SudokuInputButton(
                                onEventHandler,
                                it
                            )
                        }
                    }

                    Spacer(modifier = Modifier.size(2.dp))

                }

            }
        }


    }
}

@Composable
fun SudokuInputButton(
    onEventHandler: (ActiveGameEvent) -> Unit,
    number: Int
) {
    TextButton(
        onClick = { onEventHandler.invoke(ActiveGameEvent.OnInput(number)) },
        modifier = Modifier
            .requiredSize(56.dp)
            .padding(2.dp),
        border = BorderStroke(ButtonDefaults.OutlinedBorderSize, MaterialTheme.colors.onPrimary),

        ) {
        Text(
            text = number.toString(),
            style = inputButton.copy(color = MaterialTheme.colors.onPrimary),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun TimerText(viewModel: ActiveGameViewModel) {

    var timerState by remember {
        mutableStateOf("")
    }

    viewModel.subTimerState = {
        timerState = it.toTime()
    }

    Text(
        modifier = Modifier.requiredHeight(36.dp),
        text = timerState,
        style = activeGameSubtitle.copy(color = MaterialTheme.colors.secondary)
    )
}

@Composable
fun NewGameIcon(onEventHandler: (ActiveGameEvent) -> Unit) {
    Icon(
        imageVector = Icons.Filled.Add,
        tint = if (MaterialTheme.colors.isLight) textColorLight else textColorDark,
        contentDescription = null,
        modifier = Modifier
            .clickable(onClick = {
                onEventHandler.invoke(
                    ActiveGameEvent.OnNewGameClicked
                )
            }
            )
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .height(36.dp),
    )
}

@Composable
fun SudokuBoard(
    onEventHandler: (ActiveGameEvent) -> Unit,
    viewModel: ActiveGameViewModel,
    size: Dp
) {
    val boundary = viewModel.boundary
    val tileOffset = size.value / boundary

    //FUNCTIONS THAT DO NOT DEPEND ON CHANGED STATE ARE NOT RECOMPOSED
    var boardState by remember {
        mutableStateOf(viewModel.boardState, neverEqualPolicy())
    }

    viewModel.subBoardState = {
        boardState = it
    }

    //draw TextFields
    SudokuTextFields(
        onEventHandler = onEventHandler,
        tileOffset = tileOffset,
        boardState = boardState
    )
    //draw lines
    BoardGrid(boundary = boundary, tileOffset = tileOffset)
}

@Composable
fun SudokuTextFields(
    onEventHandler: (ActiveGameEvent) -> Unit,
    tileOffset: Float,
    boardState: HashMap<Int, SudokuTile>
) {
    boardState.values.forEach { tile ->
        var text = tile.value.toString()

        if (!tile.readOnly) {
            if (text == "0") text = ""

            Text(
                text = text,
                style = mutableSudokuSquare(tileOffset)
                    .copy(
                        color = if (MaterialTheme.colors.isLight) userInputtedNumberLight
                        else userInputtedNumberDark
                    ),
                modifier = Modifier
                    .absoluteOffset(
                        (tileOffset * (tile.x - 1)).dp,
                        (tileOffset * (tile.y - 1)).dp
                    )
                    .width(tileOffset.dp)
                    .height(tileOffset.dp)
                    .background(
                        if (tile.hasFocus) MaterialTheme.colors.onPrimary.copy(alpha = .25f)
                        else MaterialTheme.colors.surface
                    )
                    .clickable(onClick = {
                        onEventHandler.invoke(
                            ActiveGameEvent.OnTileFocused(tile.x, tile.y)
                        )
                    })
            )
        } else {
            Text(
                text = text,
                style = readOnlySudokuSquare(
                    tileOffset
                ),
                modifier = Modifier
                    .absoluteOffset(
                        (tileOffset * (tile.x - 1)).dp,
                        (tileOffset * (tile.y - 1)).dp
                    )
                    .width(tileOffset.dp)
                    .height(tileOffset.dp)
            )
        }
    }
}

@Composable
fun BoardGrid(boundary: Int, tileOffset: Float) {
    (1 until boundary).forEach {
        val width = if (it % boundary.sqrt == 0) 3.dp
        else 1.dp
        Divider(
            color = MaterialTheme.colors.primaryVariant,
            modifier = Modifier
                .absoluteOffset((tileOffset * it).dp, 0.dp)
                .fillMaxHeight()
                .width(width)
        )

        val height = if (it % boundary.sqrt == 0) 3.dp
        else 1.dp
        Divider(
            color = MaterialTheme.colors.primaryVariant,
            modifier = Modifier
                .absoluteOffset(0.dp, (tileOffset * it).dp)
                .fillMaxWidth()
                .height(height)
        )
    }
}

@Composable
fun GameCompleteContent(timerState: Long, isNewRecordState: Boolean) {
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                contentDescription = stringResource(R.string.game_complete),
                imageVector = Icons.Filled.EmojiEvents,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
                modifier = Modifier.size(128.dp, 128.dp)
            )

            if (isNewRecordState) Image(
                contentDescription = null,
                imageVector = Icons.Filled.Star,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier
                    .size(36.dp, 36.dp)
                    .absoluteOffset(y = (-16).dp)
            )
        }

        Text(
            text = stringResource(R.string.total_time),
            style = newGameSubtitle.copy(
                color = MaterialTheme.colors.secondary
            )
        )

        Text(
            text = timerState.toTime(),
            style = newGameSubtitle.copy(
                color = MaterialTheme.colors.secondary,
                fontWeight = FontWeight.Normal
            )
        )
    }
}

