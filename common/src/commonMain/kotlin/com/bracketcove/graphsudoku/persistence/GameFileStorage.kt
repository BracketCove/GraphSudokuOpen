package com.bracketcove.graphsudoku.persistence

import com.bracketcove.graphsudoku.DispatcherProvider
import com.bracketcove.graphsudoku.domain.SudokuPuzzle
import com.bracketcove.graphsudoku.domain.getHash
import kotlinx.coroutines.withContext
import java.io.*

private const val FILE_NAME = "game_state.txt"

public class GameFileStorage(fileStorageDir: String) {
    private val pathToStorageFile: File = File(fileStorageDir, FILE_NAME)

    internal suspend fun updateGame(game: SudokuPuzzle): GameFileStorageResult = withContext(
        DispatcherProvider.provideIOContext()
    ) {
        try {
            updateGameData(game)
            GameFileStorageResult.SUCCESS(game)
        } catch (e: Exception) {
            GameFileStorageResult.ERROR(e)
        }
    }

    internal suspend fun updateNode(
        x: Int,
        y: Int,
        color: Int,
        elapsedTime: Long
    ): GameFileStorageResult = withContext(DispatcherProvider.provideIOContext()) {
        try {
            val game = getGame()
            game.graph[getHash(x, y)]!!.first().color = color
            game.elapsedTime = elapsedTime
            updateGameData(game)
            GameFileStorageResult.SUCCESS(game)
        } catch (e: Exception) {

            GameFileStorageResult.ERROR(e)
        }
    }

    internal fun updateGameData(game: SudokuPuzzle) {
        try {
            val fileOutputStream = FileOutputStream(pathToStorageFile)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(game)
            objectOutputStream.close()
        } catch (e: Exception) {
            throw e
        }
    }

    internal fun getGame(): SudokuPuzzle {
        try {
            var game: SudokuPuzzle

            val fileInputStream = FileInputStream(pathToStorageFile)
            val objectInputStream = ObjectInputStream(fileInputStream)
            game = objectInputStream.readObject() as SudokuPuzzle
            objectInputStream.close()

            return (game)
        } catch (e: Exception) {
            throw e
        }
    }

    internal suspend fun getCurrentGame(): GameFileStorageResult =
        withContext(DispatcherProvider.provideIOContext()) {
            try {
                GameFileStorageResult.SUCCESS(getGame())
            } catch (e: Exception) {
                if (e is FileNotFoundException) GameFileStorageResult.EMPTY
                else GameFileStorageResult.ERROR(e)
            }
        }
}

sealed class GameFileStorageResult {
    data class SUCCESS(val game: SudokuPuzzle) : GameFileStorageResult()
    object EMPTY : GameFileStorageResult()
    data class ERROR(val error: Exception) : GameFileStorageResult()
}

