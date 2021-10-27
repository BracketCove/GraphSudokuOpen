package com.bracketcove.graphsudoku.ui.newgame

import com.bracketcove.graphsudoku.DispatcherProvider
import com.bracketcove.graphsudoku.domain.IGameRepository
import com.bracketcove.graphsudoku.ui.BaseLogic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NewGameLogic(
    private val container: INewGameContainer?,
    private val viewModel: NewGameViewModel,
    private val gameRepo: IGameRepository
) : BaseLogic<NewGameEvent>(),
    CoroutineScope {

    private val dispatcher = DispatcherProvider

    init {
        jobTracker = Job()
    }

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker

    override fun onEvent(event: NewGameEvent) {
        when (event) {
            is NewGameEvent.OnStart -> onStart()
            is NewGameEvent.OnDonePressed -> {
                viewModel.loadingState = true
                onDonePressed()
            }
            is NewGameEvent.OnSizeChanged -> viewModel.settingsState =
                viewModel.settingsState.copy(boundary = event.boundary)
            is NewGameEvent.OnDifficultyChanged -> viewModel.settingsState =
                viewModel.settingsState.copy(difficulty = event.diff)
        }
    }

    //write to both repos
    private fun onDonePressed() {
        launch {
            gameRepo.updateSettings(
                viewModel.settingsState,
                {
                    createNewGame(viewModel.settingsState.boundary)
                },
                { e ->
                    e.message?.let { it -> container?.showError(it) }
                }
            )
        }
    }

    //TODO remove boundary if unnecessary
    private fun createNewGame(boundary: Int) = launch {
        gameRepo.createNewGame(viewModel.settingsState,
            {
                jobTracker.cancel()
                container?.onDoneClick()
            },
            { e ->
                e.message?.let { it -> container?.showError(it) }
                jobTracker.cancel()
                container?.onDoneClick()
            }
        )
    }

    private fun onStart() {
        launch {
            gameRepo.getSettings(
                {
                    viewModel.settingsState = it
                    getStatistics()
                },
                { e ->
                    e.message?.let { it -> container?.showError(it) }
                    jobTracker.cancel()

                    container?.onDoneClick()
                }
            )
        }
    }

    private fun getStatistics() {
        launch {
            gameRepo.getUserRecords(
                {
                    viewModel.statisticsState = it
                    viewModel.loadingState = false
                },
                { e ->
                    e.message?.let { it -> container?.showError(it) }
                }
            )
        }
    }
}
