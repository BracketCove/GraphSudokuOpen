package com.bracketcove.graphsudoku.computationlogic

import com.bracketcove.graphsudoku.domain.SudokuPuzzle
import com.bracketcove.graphsudoku.domain.getHash
import kotlin.random.Random


/**
 * Remove a certain number of "clues" from a sudoku puzzle, relative to the desired difficulty.
 *
 * Givens are the nodes which are left colored
 * "remove" refers to the nodes which are made uncolored relative to the difficulty
 */
internal fun SudokuPuzzle.unsolve() : SudokuPuzzle {
    var remove = ((boundary * boundary) - (boundary * boundary * difficulty.modifier)).toInt()

    //bumps up givens of size 4 sudokus by 1 as their initial calculation wasn't working
    if (boundary == 4) remove--

    var counter = 0

    while (counter <= remove) {
        var colored = true
        while (colored) {
            val randX = Random.nextInt(1, boundary + 1)
            val randY = Random.nextInt(1, boundary + 1)

            val node = this.graph[getHash(randX, randY)]!!.first

            if (node.color != 0) {
                node.color = 0
                colored = false
                node.readOnly = false
                counter++
            }
        }
    }



    return this
}