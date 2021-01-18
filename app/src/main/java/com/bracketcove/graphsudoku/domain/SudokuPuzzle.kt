package com.bracketcove.graphsudoku.domain

import com.bracketcove.graphsudoku.computationlogic.buildNewSudoku
import java.io.Serializable
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * A Sudoku Game (classic variant) can be modeled as a Regular Graph. A Regular Graph is a Graph
 * where all nodes (vertices) have equal numbers of edges.
 *
 * A clique is some mathematical jargon to describe a sub-set of every two nodes that are adjacent,
 * in other words they form an "Edge"
 *
 * Edge if:
 *  - Same subgrid (e.g. 3x3, 4x4, etc.)
 *  - Same Row
 *  - Same Column
 *
 * By checking the cliques of each Node to see if a color is repeated, we may determine whether
 * or not the rules of the game have be violated.
 *
 */
data class SudokuPuzzle(
        /**
         * Note: What is the difference between HashMap vs HashSet?
         *
         * A HashMap stores key-value pairs
         * A HashSet stores only keys (assuming that's what you want to store)
         *  Therefore, we have a Map that stores <K, V>
         */
        val boundary: Int,
        val difficulty: Difficulty,
        val graph: LinkedHashMap<Int, LinkedList<SudokuNode>>
        = buildNewSudoku(boundary, difficulty).graph,
        var elapsedTime: Long = 0L
): Serializable {
        fun getValue(): LinkedHashMap<Int, LinkedList<SudokuNode>> = graph
}



