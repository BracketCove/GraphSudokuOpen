package com.bracketcove.graphsudoku.ui.activegame

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.bracketcove.graphsudoku.domain.Difficulty
import com.bracketcove.graphsudoku.domain.SudokuPuzzle
import com.bracketcove.graphsudoku.domain.getHash
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.fixedRateTimer

class ActiveGameViewModel {

    //This
    internal var subBoardState: ((HashMap<Int, SudokuTile>) -> Unit)? = null
    internal var subContentState: ((ActiveGameScreenState) -> Unit)? = null
    internal var subTimerState: ((Long) -> Unit)? = null

    internal fun updateTimerState(){
        timerState++
        subTimerState?.invoke(timerState)
    }

    internal var subIsCompleteState: ((Boolean) -> Unit)? = null

    internal var timerState: Long = 0L

    internal var difficulty = Difficulty.MEDIUM
    internal var boundary = 9
    internal var boardState: HashMap<Int, SudokuTile> = HashMap()

    internal var isCompleteState: Boolean = false
    internal var isNewRecordState: Boolean = false

    fun initializeBoardState(puzzle: SudokuPuzzle, isComplete: Boolean) {
        puzzle.graph.forEach {
            val node = it.value[0]
            boardState[it.key] = SudokuTile(
                node.x,
                node.y,
                node.color,
                false,
                node.readOnly
            )
        }

        val contentState: ActiveGameScreenState

        if (isComplete) {
            isCompleteState = true
            contentState = ActiveGameScreenState.COMPLETE
        } else {
            contentState = ActiveGameScreenState.ACTIVE
        }

        boundary = puzzle.boundary
        difficulty = puzzle.difficulty
        timerState = puzzle.elapsedTime

        subIsCompleteState?.invoke(isCompleteState)
        subContentState?.invoke(contentState)
        subBoardState?.invoke(boardState)
    }




    internal fun updateBoardState(x: Int, y: Int, value: Int, hasFocus: Boolean) {
        boardState[getHash(x, y)]?.let {
            it.value = value
            it.hasFocus = hasFocus
        }

        subBoardState?.invoke(boardState)
    }

    internal fun showLoadingState() {
        subContentState?.invoke(ActiveGameScreenState.LOADING)
    }

    internal fun updateFocusState(x: Int, y: Int) {
        boardState.values.forEach {
            if (it.x == x && it.y == y) it.hasFocus = true
            else it.hasFocus = false
        }

        subBoardState?.invoke(boardState)
    }

    fun updateCompleteState() {
        isCompleteState = true
        subContentState?.invoke(ActiveGameScreenState.COMPLETE)
    }


    /**
     * In order to simplify state management, I'll keep a virtual copy of the board which is
     * simplified from the Graph version that gets passed in.
     *
     * I made it nullable so that if it is null, we create a new one, otherwise we just update the
     * current one make things more efficient.
     */


}

/**
 * hasFocus is used when the user wants to input a value to the focused tile
 */
class SudokuTile(
    val x: Int,
    val y: Int,
    var value: Int,
    var hasFocus: Boolean,
    val readOnly: Boolean
)