package com.bracketcove.graphsudoku.domain

import java.io.Serializable

/**
 * A Node in a sudoku puzzle is denoted by:
 * - A value or color, which is an integer denoted by the set of allowed numbers in the sudoku game
 * - A list of relative x and y value, where:
 *  - top left = x0, y0 (assume 0 based indexing)
 *  - bottom right = xn-1, yn-1, where n is the largest integer in the set of allowed numbers
 *
 */
data class SudokuNode(
        val x: Int,
        val y: Int,
        var color: Int = 0,
        var readOnly: Boolean = true
): Serializable {
    /**
     * The x value is *100 to allow for uniqueness
     */
    override fun hashCode(): Int {
        return getHash(x, y)
    }
}

internal fun getHash(x: Int, y: Int):Int {
    val newX = x*100
    return "$newX$y".toInt()
}