package com.bracketcove.graphsudoku.domain

/**
 * Initially I had planned to keep Settings and Sudoku objects to be completely separate
 * repositories, but the result of that was adding a bunch of unnecessary complexity to the
 * presentation logic classes in the front end. I'd rather have a "decision maker" in the backend
 * behind this repository do the work instead.
 *
 * The only time I believe it is okay to deviate from clean architecture principles is when project
 * requirements is such that following them causes more problems than not following them!
 */
interface IGameRepository {
    suspend fun saveGame(
        elapsedTime: Long,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun updateGame(
        game: SudokuPuzzle,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    )

    /**
     * Here we care if the puzzle is complete or not, along with if the update is successful
     */
    suspend fun updateNode(
        x: Int,
        y: Int,
        color: Int,
        elapsedTime: Long,
        onSuccess: (isComplete: Boolean) -> Unit, onError: (Exception) -> Unit
    )

    suspend fun getCurrentGame(
        onSuccess: (currentGame: SudokuPuzzle, isComplete: Boolean) -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun createNewGame(
        settings: Settings,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun getSettings(onSuccess: (Settings) -> Unit, onError: (Exception) -> Unit)
    suspend fun updateSettings(
        settings: Settings,
        onSuccess: (Unit) -> Unit,
        onError: (Exception) -> Unit
    )
}

