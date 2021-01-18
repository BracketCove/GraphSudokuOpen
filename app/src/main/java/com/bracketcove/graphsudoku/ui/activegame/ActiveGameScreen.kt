package com.bracketcove.graphsudoku.ui.activegame

import androidx.compose.animation.animateAsState
import androidx.compose.animation.transition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bracketcove.graphsudoku.R
import com.bracketcove.graphsudoku.common.toTime
import com.bracketcove.graphsudoku.computationlogic.sqrt
import com.bracketcove.graphsudoku.ui.*

enum class ActiveGameScreenState {
    LOADING,
    ACTIVE,
    COMPLETE
}

@Composable
fun ActiveGameScreen(
    onEventHandler: ((ActiveGameEvent) -> Unit),
    viewModel: ActiveGameViewModel
) {

    var contentState by remember {
        mutableStateOf(
            ActiveGameScreenState.LOADING
        )
    }

    viewModel.subContentState = {
        contentState = it
    }


    val contentStateTransition = transition(
        activeGameViewTransition(),
        contentState
    )

    GraphSudokuTheme {
        Column(Modifier
            .background(MaterialTheme.colors.primary)
        ) {
            ActiveGameToolbar(
                clickHandler = {
                    onEventHandler.invoke(
                        ActiveGameEvent.OnNewGameClicked
                    )
                }
            )

            when (contentState) {
                ActiveGameScreenState.LOADING -> Box(
                    Modifier.alpha(
                        contentStateTransition[loadingAlphaKey]
                    )
                ) {
                    LoadingScreen()
                }

                ActiveGameScreenState.ACTIVE -> Box(
                    Modifier.alpha(
                        contentStateTransition[activeAlphaKey]
                    )
                ) {
                    GameContent(
                        onEventHandler,
                        viewModel
                    )
                }

                ActiveGameScreenState.COMPLETE -> Box(
                    Modifier.alpha(
                        contentStateTransition[completeAlphaKey]
                    )
                ) {
                    GameCompleteContent(
                        viewModel.timerState,
                        viewModel.isNewRecordState
                    )
                }
            }

            Box(
                Modifier.fillMaxWidth()
                    .height(90.dp)
            ) {
                BannerAd()
            }
        }
    }
}


@Composable
fun GameCompleteContent(timerState: Long, isNewRecordState: Boolean) {
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colors.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                imageVector = Icons.Filled.EmojiEvents,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary),
                modifier = Modifier.preferredSize(128.dp, 128.dp)
            )

            if (isNewRecordState) Image(
                imageVector = Icons.Filled.Star,
                colorFilter = ColorFilter.tint(Color.Black),
                modifier = Modifier
                    .preferredSize(36.dp, 36.dp)
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

@OptIn(ExperimentalLayout::class)
@Composable
fun GameContent(
    onEventHandler: (ActiveGameEvent) -> Unit,
    viewModel: ActiveGameViewModel
) {
    //Set background color of content
    Surface(
        Modifier.fillMaxSize()
    ) {
        WithConstraints {
            val size = with(AmbientDensity.current) {
                (constraints.maxWidth).toDp()
            }

            ConstraintLayout(
                Modifier.background(MaterialTheme.colors.primary)
            ) {
                val (board, timer, diff, inputs, adBanner) = createRefs()

                var isComplete by remember {
                    mutableStateOf(false)
                }

                viewModel.subIsCompleteState = { isComplete = it }

                //Add Puzzle board component
                Box(Modifier
                    .constrainAs(board) {
                        top.linkTo(parent.top)
                    }
                    .background(MaterialTheme.colors.surface)
                    .size(size)
                    .border(
                        width = 2.dp, color = animateAsState(
                            if (isComplete) victoryColor
                            else MaterialTheme.colors.primaryVariant
                        ).value
                    )
                ) {
                    SudokuBoard(
                        onEventHandler,
                        viewModel,
                        size
                    )
                }


                //TODO implement timer
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
                            imageVector = Icons.Filled.Star,
                            tint = MaterialTheme.colors.secondary,
                            modifier = Modifier.preferredSize(36.dp)
                        )
                    }
                }
                //TODO implement difficulty text

                //TODO implement input buttons
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .constrainAs(inputs) {
                            top.linkTo(timer.bottom)
                        }
                ) {
                    FlowRow(
                        mainAxisSpacing = 4.dp,
                        crossAxisSpacing = 4.dp
                    ) {
                        (0..viewModel.boundary).forEach {
                            SudokuInputButton(
                                onEventHandler,
                                it
                            )
                        }
                    }
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
    Button(
        onClick = { onEventHandler.invoke(ActiveGameEvent.OnInput(number)) },
        modifier = Modifier.preferredSize(56.dp),
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
        text = timerState,
        style = activeGameSubtitle.copy(color = MaterialTheme.colors.secondary)
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

@Preview(
    device = Devices.PIXEL_4_XL
)
@Composable
fun PreviewContent() {
    GraphSudokuTheme {
        GameContent(onEventHandler = { /*TODO*/ },
            viewModel = ActiveGameViewModel().apply {

            }
        )
    }
}


@Preview
@Composable
fun PreviewCompleteContent() {
    GraphSudokuTheme {
        GameCompleteContent(
            60,
            true
        )
    }
}