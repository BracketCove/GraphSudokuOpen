package com.bracketcove.graphsudoku.ui.newgame

import android.util.Log
import com.bracketcove.graphsudoku.common.BaseLogic
import com.bracketcove.graphsudoku.common.DispatcherProvider
import com.bracketcove.graphsudoku.domain.Difficulty
import com.bracketcove.graphsudoku.domain.IGameRepository
import com.bracketcove.graphsudoku.domain.IStatisticsRepository
import com.bracketcove.graphsudoku.domain.Messages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NewGameLogic(
    private val container: NewGameContainer?,
    private val viewModel: NewGameViewModel,
    private val gameRepo: IGameRepository,
    private val statsRepo: IStatisticsRepository,
    dispatcher: DispatcherProvider
) : BaseLogic<NewGameEvent>(dispatcher),
    CoroutineScope {

    init {
        jobTracker = Job()
    }

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker

    override fun onEvent(event: NewGameEvent) {
        when (event) {
            is NewGameEvent.OnStart -> onStart()
            is NewGameEvent.OnDonePressed -> onDonePressed()
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
                {
                    container?.showMessage(Messages.IO_ERROR_UPDATE)
                }
            )
        }
    }

    private fun createNewGame(boundary: Int) = launch {
        gameRepo.createNewGame(viewModel.settingsState,
            {
                jobTracker.cancel()
                container?.onDoneClick()
            },
            {
                container?.showMessage(Messages.IO_ERROR_UPDATE)
                jobTracker.cancel()
                container?.onDoneClick()
            }
        )
    }

    private fun onStart() {
        launch {
            gameRepo.getSettings(
                {
                    //TODO change loading state
                    viewModel.settingsState = it
                    getStatistics()
                },
                {
                    jobTracker.cancel()
                    container?.showMessage(Messages.IO_ERROR_READ)
                    container?.onDoneClick()
                }
            )
        }
    }

    private fun getStatistics() {
        launch {
            statsRepo.getStatistics(
                {
                    viewModel.statisticsState = it
                    viewModel.loadingState = false
                },
                {
                    container?.showMessage(Messages.IO_ERROR_READ)
                }
            )
        }
    }
}
