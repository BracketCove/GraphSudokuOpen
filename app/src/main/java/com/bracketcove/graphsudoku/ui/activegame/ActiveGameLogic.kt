package com.bracketcove.graphsudoku.ui.activegame

import com.bracketcove.graphsudoku.common.BaseLogic
import com.bracketcove.graphsudoku.common.DispatcherProvider
import com.bracketcove.graphsudoku.domain.IGameRepository
import com.bracketcove.graphsudoku.domain.IStatisticsRepository
import com.bracketcove.graphsudoku.domain.Messages
import com.bracketcove.graphsudoku.domain.SudokuPuzzle
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * I noticed that subtracting 1 from elapsed time lead the Timer in the UI to seem more consistent;
 * otherwise it would jump ahead by 2ish seconds which looking wierd.
 */
private const val TIME_OFFSET = 1

class ActiveGameLogic(
    private val container: ActiveGameContainer?,
    private val viewModel: ActiveGameViewModel,
    private val gameRepo: IGameRepository,
    private val statsRepo: IStatisticsRepository,
    dispatcher: DispatcherProvider
) : BaseLogic<ActiveGameEvent>(dispatcher),
    CoroutineScope {


    private var timerTracker: Job? = null

    init {
        //allows cancellation
        jobTracker = Job()
    }

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker

    inline fun startCoroutineTimer(
        delayMillis: Long = 0,
        repeatMillis: Long = 1000,
        crossinline action: () -> Unit
    ) = launch {
        delay(delayMillis)
        if (repeatMillis > 0) {
            while (true) {
                action()
                delay(repeatMillis)
            }
        } else {
            action()
        }
    }

    override fun onEvent(event: ActiveGameEvent) {
        when (event) {
            is ActiveGameEvent.OnInput -> onInput(
                event.input,
                viewModel.timerState
            )
            ActiveGameEvent.OnNewGameClicked -> onNewGameClicked()
            ActiveGameEvent.OnStart -> onStart()
            ActiveGameEvent.OnStop -> onStop()
            is ActiveGameEvent.OnTileFocused -> onTileFocused(event.x, event.y)
        }
    }

    private fun onTileFocused(x: Int, y: Int) {
        viewModel.updateFocusState(x, y)
    }

    private fun onStop() {
        if (!viewModel.isCompleteState) {
            launch {
                gameRepo.saveGame(
                    viewModel.timerState - TIME_OFFSET,
                    { cancelStuff() },
                    {
                        cancelStuff()
                        container?.showMessage(Messages.IO_ERROR_UPDATE)
                    }
                )
            }
        } else {
            cancelStuff()
        }
    }

    private fun cancelStuff() {
        if (timerTracker?.isCancelled == false) timerTracker?.cancel()
        jobTracker.cancel()
    }

    /**
     * get current game
     */
    private fun onStart() =
        launch {
            gameRepo.getCurrentGame(
                { puzzle, isComplete ->
                    viewModel.initializeBoardState(
                        puzzle,
                        isComplete
                    )
                    if (!isComplete) timerTracker = startCoroutineTimer {
                        viewModel.updateTimerState()
                    }
                },
                {
                    container?.showMessage(Messages.IO_ERROR_READ)
                    container?.restart()
                }
            )
        }


    private fun onNewGameClicked() = launch {
        if (!viewModel.isCompleteState) {
            gameRepo.getCurrentGame(
                { puzzle, _ ->
                    updateWithTime(puzzle)
                },
                {
                    container?.showMessage(Messages.IO_ERROR_UPDATE)
                }
            )
        } else {
            navigateToNewGame()
        }

    }

    private fun updateWithTime(puzzle: SudokuPuzzle) = launch {
        gameRepo.updateGame(puzzle.copy(elapsedTime = viewModel.timerState - TIME_OFFSET),
            { navigateToNewGame() },
            {
                navigateToNewGame()
                container?.showMessage(Messages.IO_ERROR_UPDATE)
            }
        )
    }

    private fun navigateToNewGame() {
        cancelStuff()
        container?.onNewGameClick()
    }

    /**
     * Check for any tile which hasFocus, and if so, write that value
     */
    private fun onInput(input: Int, elapsedTime: Long) = launch {
        var focusedTile: SudokuTile? = null
        viewModel.boardState.values.forEach {
            if (it.hasFocus) focusedTile = it
        }

        if (focusedTile != null) {
            gameRepo.updateNode(focusedTile!!.x,
                focusedTile!!.y,
                input,
                elapsedTime,
                //success
                { isComplete ->
                    focusedTile?.let {
                        viewModel.updateBoardState(
                            it.x,
                            it.y,
                            input,
                            false
                        )
                    }
                    if (isComplete) {
                        timerTracker?.cancel()
                        checkIfNewRecord()
                    }
                },
                //error
                {
                    container?.showMessage(Messages.IO_ERROR_UPDATE)
                }
            )
        }

    }

    private fun checkIfNewRecord() = launch {
        statsRepo.updateStatistic(
            viewModel.timerState,
            viewModel.difficulty,
            viewModel.boundary,
            { isRecord ->
                viewModel.isNewRecordState = isRecord
                viewModel.updateCompleteState()
            },
            {
                container?.showMessage(Messages.IO_ERROR_UPDATE)
                viewModel.updateCompleteState()
            }
        )
    }
}