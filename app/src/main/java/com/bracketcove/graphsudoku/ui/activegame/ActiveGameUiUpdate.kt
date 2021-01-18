package com.bracketcove.graphsudoku.ui.activegame

import com.bracketcove.graphsudoku.domain.SudokuPuzzle

/**
 * States:
 * - Loading Screen
 * - Current Active Noncomplete game is loaded
 * - Current Complete Game is loaded
 *
 * Note: I want to avoid having to recompose the entire UI every time the user makes a single
 * change to the UI.
 */
data class ActiveGameUiUpdate(
    val state: ActiveGameScreenState,
    val puzzle: SudokuPuzzle?,
    val gameCompleteStringRes: String = "R.string.game_complete"
)