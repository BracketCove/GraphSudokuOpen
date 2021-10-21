package com.bracketcove.graphsudoku.persistence

import com.bracketcove.graphsudoku.domain.*

class GameRepositoryImpl (
    private val gameStorage: IGameDataStorage,
    private val settingsStorage: ISettingsStorage
) : IGameRepository {
    override suspend fun saveGame(
        elapsedTime: Long,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    ) {
        when (val getCurrentGameResult = gameStorage.getCurrentGame()) {
            is GameStorageResult.OnSuccess -> {
                gameStorage.updateGame(
                    getCurrentGameResult.currentGame.copy(
                        elapsedTime = elapsedTime
                    )
                )

                onSuccess(Unit)
            }
            is GameStorageResult.OnError -> {
                onError(getCurrentGameResult.exception)
            }
        }
    }

    override suspend fun updateGame(
        game: SudokuPuzzle,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    ) {
        when (val updateGameResult: GameStorageResult = gameStorage.updateGame(game)) {
            is GameStorageResult.OnSuccess -> onSuccess(Unit)
            is GameStorageResult.OnError -> onError(updateGameResult.exception)
        }
    }

    override suspend fun updateNode(
        x: Int,
        y: Int,
        color: Int,
        elapsedTime: Long,
        onSuccess: (isComplete: Boolean) -> Unit,
        onError: (Exception) -> Unit
    ) {
        when (val result = gameStorage.updateNode(x, y, color, elapsedTime)) {
            is GameStorageResult.OnSuccess -> onSuccess(
                puzzleIsComplete(result.currentGame)
            )
            is GameStorageResult.OnError -> onError(
                result.exception
            )
        }
    }

    /**
     * This is mainly where this repository becomes important. I didn't want the front end decision
     * maker to make all of these decisions, but adding in an interactor/usecase/transaction script
     * is overkill for an androidclient of this size.
     * 1. Request current game
     * 2a. Current game returns onSuccess; forward to caller onSuccess
     * 2b. Current game returns onError
     * 3b. Request current Settings from settingsStorage
     * 4c. settingsStorage returns onSuccess
     * 4d. settingsStorage returns onError
     * 5c. Write game update to gameStorage (to ensure consistent state between front and back end)
     * 5d. We're screwed at this point ¯\_(ツ)_/¯ ; forward to caller onError
     * 6e. gameStorage returns onSuccess; forward to caller onSuccess
     * 6f. gameStorage returns onError; forward to caller onError
     */
    override suspend fun getCurrentGame(
        onSuccess: (currentGame: SudokuPuzzle, isComplete: Boolean) -> Unit,
        onError: (Exception) -> Unit
    ) {
        when (val getCurrentGameResult = gameStorage.getCurrentGame()) {
            is GameStorageResult.OnSuccess -> onSuccess(
                getCurrentGameResult.currentGame,
                puzzleIsComplete(
                    getCurrentGameResult.currentGame
                )
            )
            is GameStorageResult.OnError -> {
                when (val getSettingsResult = settingsStorage.getSettings()) {
                    is SettingsStorageResult.OnSuccess -> {
                        when (val updateGameResult =
                            createAndWriteNewGame(getSettingsResult.settings)) {
                            is GameStorageResult.OnSuccess -> onSuccess(
                                updateGameResult.currentGame,
                                puzzleIsComplete(
                                    updateGameResult.currentGame
                                )
                            )

                            is GameStorageResult.OnError -> onError(updateGameResult.exception)
                        }
                    }
                    is SettingsStorageResult.OnError -> onError(getSettingsResult.exception)
                }
            }
        }
    }

    override suspend fun createNewGame(
        settings: Settings,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    ) {
       when (val updateSettingsResult = settingsStorage.updateSettings(settings)) {
           SettingsStorageResult.OnComplete -> {
               when (val updateGameResult = createAndWriteNewGame(settings)) {
                   is GameStorageResult.OnSuccess -> onSuccess(Unit)
                   is GameStorageResult.OnError -> onError(updateGameResult.exception)
               }
           }
           is SettingsStorageResult.OnError -> onError(updateSettingsResult.exception)
       }
    }

    private suspend fun createAndWriteNewGame(settings: Settings): GameStorageResult {
        return gameStorage.updateGame(
            SudokuPuzzle(
                settings.boundary,
                settings.difficulty
            )
        )
    }

    override suspend fun getSettings(onSuccess: (Settings) -> Unit, onError: (Exception) -> Unit) {
       when (val getSettingsResult = settingsStorage.getSettings()) {
           is SettingsStorageResult.OnError -> onError(getSettingsResult.exception)
           is SettingsStorageResult.OnSuccess -> onSuccess(getSettingsResult.settings)
       }
    }

    override suspend fun updateSettings(
        settings: Settings,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    ) {
        settingsStorage.updateSettings(settings)
        onSuccess(Unit)
    }
}