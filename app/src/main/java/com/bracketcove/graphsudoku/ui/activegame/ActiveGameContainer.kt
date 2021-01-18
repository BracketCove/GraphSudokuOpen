package com.bracketcove.graphsudoku.ui.activegame

import com.bracketcove.graphsudoku.domain.Messages
import com.bracketcove.graphsudoku.domain.SudokuPuzzle

interface ActiveGameContainer {
    fun showMessage(message: Messages)
    fun onNewGameClick()
    fun restart()
}