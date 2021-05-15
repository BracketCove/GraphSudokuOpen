package com.bracketcove.graphsudoku.persistence

import com.bracketcove.graphsudoku.domain.GameStorageResult
import com.bracketcove.graphsudoku.domain.IGameDataStorage
import com.bracketcove.graphsudoku.domain.SudokuPuzzle
import com.bracketcove.graphsudoku.domain.getHash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

private const val FILE_NAME = "game_state.txt"

class LocalGameStorageImpl(
    fileStorageDirectory: String,
    private val pathToStorageFile: File = File(fileStorageDirectory, FILE_NAME)
) : IGameDataStorage {

    override suspend fun updateGame(
        game: SudokuPuzzle,
    ): GameStorageResult = withContext(Dispatchers.IO) {
        try {
            updateGameData(game)
            GameStorageResult.OnSuccess(game)
        } catch (e: Exception) {
            GameStorageResult.OnError(e)
        }
    }

    private fun updateGameData(game: SudokuPuzzle) {
        try {
            val fileOutputStream = FileOutputStream(pathToStorageFile)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(game)
            objectOutputStream.close()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateNode(
        x: Int,
        y: Int,
        color: Int,
        elapsedTime: Long
    ): GameStorageResult = withContext(Dispatchers.IO) {
        try {
            val game = getGame()
            game.graph[getHash(x, y)]!!.first.color = color
            game.elapsedTime = elapsedTime
            updateGameData(game)
            GameStorageResult.OnSuccess(game)
        } catch (e: Exception) {
            GameStorageResult.OnError(e)
        }
    }

    override suspend fun getCurrentGame(): GameStorageResult = withContext(Dispatchers.IO) {
        try {
            GameStorageResult.OnSuccess(getGame())
        } catch (e: Exception) {
            GameStorageResult.OnError(e)
        }
    }

    private fun getGame(): SudokuPuzzle {
        try {
            var game: SudokuPuzzle

            val fileInputStream = FileInputStream(pathToStorageFile)
            val objectInputStream = ObjectInputStream(fileInputStream)
            game = objectInputStream.readObject() as SudokuPuzzle
            objectInputStream.close()

            return (game)
        } catch (e: FileNotFoundException) {
            throw e
        }
    }
}