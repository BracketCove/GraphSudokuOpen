package com.bracketcove.graphsudoku.domain

import com.bracketcove.graphsudoku.computationlogic.buildNewSudoku
import java.io.Serializable

data class SudokuPuzzle(
    val boundary: Int,
    val difficulty: DIFFICULTY,
    val graph: HashMap<Int, List<SudokuNode>>
    = buildNewSudoku(boundary, difficulty).graph,
    var elapsedTime: Long = 0L
): Serializable {
    fun getValue(): HashMap<Int, List<SudokuNode>> = graph
}
