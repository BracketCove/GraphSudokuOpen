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

/**
 * This enum represents different states which this feature of the user interface
 * can possess.
 *
 * The actual state is held in the ViewModel, but we will see how we can update our composable UI
 * by binding to the ViewModel's Function Types we created in the previous part of the tutorial
 */
enum class ActiveGameScreenState {
    LOADING,
    ACTIVE,
    COMPLETE
}

/**
 * ActiveGameScreen represents the Root composable in this hierarchy of composables.
 *
 * It has the responsibility of setting up the core elements of the UI, and also
 * animating between them.
 *
 */
@Composable
fun ActiveGameScreen(
    //The event handler Function Type reference is how we call back to the Presentation Logic when
    //the user interacts with the application. It must be passed down to any composable which has
    //such interactions.
    onEventHandler: (ActiveGameEvent) -> Unit,
    //We also pass in the ViewModel which is how we actually give the data to our UI
    viewModel: ActiveGameViewModel
) {

    //In very simple language, whenever we have some kind of data, or state, which may change at
    //runtime, we want to wrap that data in a remember delegate. This tells the tells the compose
    //library under the hood, to watch for changes, and to redraw the UI if a change occurs.
    val contentTransitionState = remember {
        //Now, MutableTransitionState is used specifically for animations here, so don't use this
        //everywhere. We will see a more general purpose example of remembered state later on.
        MutableTransitionState(
            ActiveGameScreenState.LOADING
        )
    }

    //Our remember delegate prepares compose for updates, but we also need a way to actually update
    //the value. We do this by binding a lambda expression to one of the Function Types which
    //our ViewModel possesses. When one of those functions is invoked in the ViewModel,
    //the program automatically jumps to and executes this code within our composable.
    //This is what actually triggers the Recomposition.
    viewModel.subContentState = {
        contentTransitionState.targetState = it
    }

    //We have a remembered transition state, and a way to update that state from the ViewModel.
    //Now we need to set up the transitions animations themselves. This is where you can get as
    //creative as you like. In this app, each content state has it's own composable associated
    //with it. We animate between them simply by changing the alpha, or transparency.
    val transition = updateTransition(contentTransitionState)

    val loadingAlpha by transition.animateFloat(
        //The transition spec tells compose details about what the animation should
        //look like. Essentially, this means we don't have to write our own mathematical
        //instructions, which is great for someone like me who sucks at arithmetic.
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

    //One option for compose, is to use a Scaffold as a skeleton for your UI. I personally prefer
    //to do this myself since it is not difficult at all, and doesn't hide anything from me.
    Column(
        Modifier
            .background(MaterialTheme.colors.primary)
            .fillMaxHeight()
    ) {

        AppToolbar(
            modifier = Modifier
                .wrapContentHeight(),
            title = stringResource(R.string.app_name),
        ) {
            //we will create this later of course
            NewGameIcon(onEventHandler = onEventHandler)
        }

        //Below the toolbar, we have the main content of this Screen, which can have three different
        //states.
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 4.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            //Each time a recomposition occurs, this when statement will be executed again.
            when (contentTransitionState.currentState) {
                ActiveGameScreenState.ACTIVE -> Box(
                    //These values will change when the transition animation occurs, thus fading
                    //out the previous content state, and fading in the new one.
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

/**
 * As explained in a previous part of the tutorial, by creating our Toolbar Icon here and passing
 * it into the AppToolbar composable, we make AppToolbar reusable.
 */
@Composable
fun NewGameIcon(onEventHandler: (ActiveGameEvent) -> Unit) {
    Icon(
        //these icons come from the compose material library; I highly recommend it
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

/**
 * The most complex part of the UI comes from an Active sudoku game. A 9x9 puzzle has 81 different
 * Text composables, which is a large number of widgets. The way I went about writing this
 * composable was to think of each part of the Sudoku Game as a layer.
 *
 * Be sure to avoid writing God Composables by making usage of Helper functions which break down
 * the UI into the smallest reasonable parts.
 */
@Composable
fun GameContent(
    onEventHandler: (ActiveGameEvent) -> Unit,
    viewModel: ActiveGameViewModel
) {

    //Box with constraints is a composable wrapper, which gives us information about the height,
    //width, and other measurements. We can use that information within it's lambda expression.
    BoxWithConstraints {

        //We need to know the screen width in order to determine how wide and tall the sudoku
        //board should be. Here we ask for the max width of this constraint layout, but we need
        //that value to be in Density Independent pixels, and it needs to be relative to the
        //density of the screen as well. That's where the toDp() extension functions comes in,
        //and it uses the LocalDensity to determine that value.
        val screenWidth = with(LocalDensity.current) {
            constraints.maxWidth.toDp()
        }

        //The margin of the board also needs to change based on the screen density.
        //I arrived at these values simply by testing the app on various densities.
        val margin = with(LocalDensity.current) {
            when {
                constraints.maxHeight.toDp().value < 500f -> 20
                constraints.maxHeight.toDp().value < 550f -> 8
                else -> 0
            }
        }

        //Next, we will write a ConstraintLayout, which is a totally awesome way to manage
        //dynamic Layouts.
        ConstraintLayout {

            //Now, in order to constraint composables to each other, we need a way for them to
            //reference each other. This is equivalent to settings IDs for XML views. First we
            //create these references, and you will see how we bind them later on.
            val (board, timer, diff, inputs) = createRefs()

            //Let's create a Layout container for the Puzzle Board.
            Box(Modifier
                .constrainAs(board) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .background(MaterialTheme.colors.surface)
                .size(screenWidth - margin.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colors.primaryVariant
                )
            ) {

                //we will write the puzzle board itself in a minute
                SudokuBoard(
                    onEventHandler,
                    viewModel,
                    screenWidth - margin.dp
                )
            }

            //Next, we create a layout container for the countdown timer
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

            //This container is for some icons which indicate the difficulty
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(diff) {
                        top.linkTo(board.bottom)
                        end.linkTo(parent.end)
                    }
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

            //this container holds the input buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .constrainAs(inputs) {
                        top.linkTo(timer.bottom)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Now, hard coding this is kind of bad practice, but the reason is that the
                //Compose team deprecated FlowRow which worked perfectly for this situation X(
                if (viewModel.boundary == 4) {
                    InputButtonRow(
                        (0..4).toList(),
                        onEventHandler
                    )
                } else {
                    InputButtonRow(
                        (0..4).toList(),
                        onEventHandler
                    )

                    InputButtonRow(
                        (5..9).toList(),
                        onEventHandler
                    )
                }
            }
        }
    }
}

@Composable
fun SudokuBoard(
    onEventHandler: (ActiveGameEvent) -> Unit,
    viewModel: ActiveGameViewModel,
    size: Dp
) {
    val boundary = viewModel.boundary

    //We want to evenly distribute the screen real estate for each sudoku tile
    val tileOffset = size.value / boundary

    //neverEqualPolicy ensures that even minor changes in the state like hasFocus actually triggers
    //a recomposition
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

/**
 * Here we render the text fields which represent tiles in the puzzle. They can either be readOnly
 * or mutable, thus meaning that we need to render them differently.
 */
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


/**
 * This will draw the gridlines that separate sudoku puzzles.
 * To make it more obvious to the User which subgrids are which, we draw different borders to
 * separate 4x4 or 9x9 sub grids.
 */
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
fun InputButtonRow(
    numbers: List<Int>,
    onEventHandler: (ActiveGameEvent) -> Unit
) {
    Row {
        numbers.forEach {
            SudokuInputButton(
                onEventHandler,
                it
            )
        }
    }

    //margin between rows
    Spacer(modifier = Modifier.size(2.dp))
}

@Composable
fun SudokuInputButton(
    onEventHandler: (ActiveGameEvent) -> Unit,
    number: Int
) {
    //This wrapper allows us to style a nice looking button instead of just adding onClick on a
    //text composable
    TextButton(
        //Here is how we handle click events using onClick and our onEventHandler
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

/**
 * This screen represents when the user has completed a puzzle.
 */
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

