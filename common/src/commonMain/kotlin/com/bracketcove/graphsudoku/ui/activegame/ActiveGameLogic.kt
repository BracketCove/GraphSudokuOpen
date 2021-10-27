package com.bracketcove.graphsudoku.ui.activegame


import com.bracketcove.graphsudoku.DispatcherProvider
import com.bracketcove.graphsudoku.domain.IGameRepository
import com.bracketcove.graphsudoku.domain.SudokuPuzzle
import com.bracketcove.graphsudoku.ui.BaseLogic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ActiveGameLogic(
    private val container: IActiveGameContainer?,
    private val viewModel: ActiveGameViewModel,
    private val gameRepo: IGameRepository
) : BaseLogic<ActiveGameEvent>(), CoroutineScope {

    private val dispatcher = DispatcherProvider

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker

    init {
        jobTracker = Job()
    }

    inline fun startCoroutineTimer(
        crossinline action: () -> Unit
    ) = launch {
        while (true) {
            action()
            delay(1000)
        }
    }

    private var timerTracker: Job? = null

    private val Long.timeOffset: Long
        get() {
            return if (this <= 0) 0
            else this - 1
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
                    viewModel.timerState.timeOffset,
                    { cancelStuff() },
                    { e ->
                        e.message?.let { it -> container?.showError(it) }
                        cancelStuff()
                    }
                )
            }
        } else {
            cancelStuff()
        }
    }

    private fun onStart() = launch {
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
            { e ->
                e.message?.let { it -> container?.showError(it) }
                container?.onNewGameClick()
            }
        )
    }

    private fun onNewGameClicked() = launch {
        viewModel.showLoadingState()

        if (!viewModel.isCompleteState) {
            gameRepo.getCurrentGame(
                { puzzle, _ ->
                    updateWithTime(puzzle)
                },
                { e ->
                    e.message?.let { it -> container?.showError(it) }
                }
            )
        } else {
            navigateToNewGame()
        }
    }

    private fun updateWithTime(puzzle: SudokuPuzzle) = launch {
        gameRepo.updateGame(
            puzzle.copy(elapsedTime = viewModel.timerState.timeOffset),
            { navigateToNewGame() },
            { e ->
                e.message?.let { it -> container?.showError(it) }
                navigateToNewGame()
            }
        )
    }

    private fun navigateToNewGame() {
        cancelStuff()
        container?.onNewGameClick()
    }

    private fun cancelStuff() {
        if (timerTracker?.isCancelled == false) timerTracker?.cancel()
        jobTracker.cancel()
    }

    private fun onInput(input: Int, elapsedTime: Long) = launch {
        var focusedTile: SudokuTile? = null
        viewModel.boardState.values.forEach {
            if (it.hasFocus) focusedTile = it
        }

        if (focusedTile != null) {
            gameRepo.updateNode(
                focusedTile!!.x,
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
                { e ->
                    e.message?.let { it -> container?.showError(it) }
                }
            )
        }
    }

    private fun checkIfNewRecord() = launch {
        gameRepo.updateUserRecord(
            viewModel.timerState,
            viewModel.boundary,
            viewModel.difficulty,

            //success
            { isRecord ->
                viewModel.isNewRecordState = isRecord
                viewModel.updateCompleteState()
            },
            //error
            { e ->
                e.message?.let { it -> container?.showError(it) }
                viewModel.updateCompleteState()
            }
        )
    }
}