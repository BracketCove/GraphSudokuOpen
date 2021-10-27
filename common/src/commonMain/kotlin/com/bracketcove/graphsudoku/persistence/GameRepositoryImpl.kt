package com.bracketcove.graphsudoku.persistence

import com.bracketcove.graphsudoku.computationlogic.puzzleIsComplete
import com.bracketcove.graphsudoku.domain.*

const val DEFAULT_BOUNDARY = 9
val DEFAULT_DIFFICULTY = DIFFICULTY.MEDIUM

class GameRepositoryImpl(
    private val gameStorage: GameFileStorage,
    private val userDataStorage: UserDataStorage
) : IGameRepository {
    override suspend fun saveGame(
        elapsedTime: Long,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    ) {
        when (val result = gameStorage.getCurrentGame()) {
            is GameFileStorageResult.SUCCESS -> {
                gameStorage.updateGame(result.game.copy(elapsedTime = elapsedTime))
            }
            is GameFileStorageResult.ERROR -> {
                onError(result.error)
            }
        }
    }

    override suspend fun updateGame(
        game: SudokuPuzzle,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    ) {
        when (val result = gameStorage.updateGame(game)) {
            is GameFileStorageResult.SUCCESS -> onSuccess(Unit)
            is GameFileStorageResult.ERROR -> onError(result.error)
        }
    }

    override suspend fun createNewGame(
        settings: Settings,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    ) {
        when (val settingsResult = userDataStorage.updateSettings(settings)) {
            is UserDataStorageResult.ERROR -> {
                onError(settingsResult.error)
                return
            }
        }

        when (
            val result = gameStorage.updateGame(
                SudokuPuzzle(
                    settings.boundary,
                    settings.difficulty
                )
            )
        ) {
            is GameFileStorageResult.SUCCESS -> onSuccess(Unit)
            is GameFileStorageResult.ERROR -> onError(result.error)
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
            is GameFileStorageResult.SUCCESS -> onSuccess(
                puzzleIsComplete(result.game)
            )
            is GameFileStorageResult.ERROR -> onError(
                result.error
            )
        }
    }

    override suspend fun getCurrentGame(
        onSuccess: (currentGame: SudokuPuzzle, isComplete: Boolean) -> Unit,
        onError: (Exception) -> Unit
    ) {
        when (val getCurrentResult = gameStorage.getCurrentGame()) {
            is GameFileStorageResult.SUCCESS -> {
                onSuccess(
                    getCurrentResult.game,
                    puzzleIsComplete(getCurrentResult.game)
                )

                return
            }
            is GameFileStorageResult.ERROR -> {
                onError(getCurrentResult.error)
                return
            }
        }

        //Assume GameFileStorageResult.EMPTY from here on
        //First, update the settings and load default records
        try {
            userDataStorage.createDefaults()
        } catch (e: Exception) {
            onError(e)
        }

        val game = SudokuPuzzle(DEFAULT_BOUNDARY, DEFAULT_DIFFICULTY)

        when (val result = gameStorage.updateGame(game)) {
            is GameFileStorageResult.SUCCESS -> onSuccess(
                result.game,
                puzzleIsComplete(result.game)
            )
            is GameFileStorageResult.ERROR -> onError(result.error)
        }
    }

    override suspend fun getSettings(onSuccess: (Settings) -> Unit, onError: (Exception) -> Unit) {
        val result = userDataStorage.getSettings()

        when (result) {
            is UserDataStorageResult.SUCCESS_SETTINGS -> onSuccess(result.settings)
            is UserDataStorageResult.ERROR -> onError(result.error)
            else -> onError(Exception("Invalid result returned from getSettings"))
        }
    }

    override suspend fun updateSettings(
        settings: Settings,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val result = userDataStorage.updateSettings(settings)

        when (result) {
            is UserDataStorageResult.SUCCESS_SETTINGS -> onSuccess(Unit)
            is UserDataStorageResult.ERROR -> onError(result.error)
            else -> onError(Exception("Invalid result returned from updateSettings"))
        }
    }

    override suspend fun getUserRecords(
        onSuccess: (UserRecords) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val result = userDataStorage.getRecords()

        when (result) {
            is UserDataStorageResult.SUCCESS_RECORDS -> onSuccess(result.records)
            is UserDataStorageResult.ERROR -> onError(result.error)
            else -> onError(Exception("Invalid result returned from getRecords"))
        }
    }

    override suspend fun updateUserRecord(
        record: Long,
        boundary: Int,
        difficulty: DIFFICULTY,
        onSuccess: (isRecord: Boolean) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val result = userDataStorage.updateRecord(record, boundary, difficulty)

        when (result) {
            is UserDataStorageResult.SUCCESS_RECORDS -> onSuccess(result.isRecord)
            is UserDataStorageResult.ERROR -> onError(result.error)
            else -> onError(Exception("Invalid result returned from updateSettings"))
        }
    }
}