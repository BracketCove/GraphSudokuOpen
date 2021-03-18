package com.bracketcove.graphsudoku.computationlogic

import com.bracketcove.graphsudoku.domain.SudokuNode
import com.bracketcove.graphsudoku.domain.SudokuPuzzle
import com.bracketcove.graphsudoku.domain.getHash
import java.util.*
import kotlin.math.sqrt

internal val Int.sqrt: Int
    get() = sqrt(this.toDouble()).toInt()


internal fun puzzleIsComplete(puzzle: SudokuPuzzle): Boolean {
    return when {
        !puzzleIsValid(puzzle) -> false
        allSquaresAreNotEmpty(puzzle) -> false
        else -> true
    }
}

internal fun puzzleIsValid(puzzle: SudokuPuzzle): Boolean {
    return when {
        rowsAreInvalid(puzzle) -> false
        columnsAreInvalid(puzzle) -> false
        subgridsAreInvalid(puzzle) -> false
        else -> true
    }
}

/**
 * for each y position, check all nodes in the adjacencyList which have same y value for repeats.
 * We only need the adjacency list at each value of y, as opposed to iterating through every cell.
 * X is always 1, which is converted to 10 as per the hash value
 * e.g.
 * []
 *
 */
internal fun rowsAreInvalid(puzzle: SudokuPuzzle): Boolean {
    //for each adjacencyList
    (1..puzzle.boundary).forEach { row ->
        val nodeList: LinkedList<SudokuNode> = puzzle.graph[getHash(1, row)]!!
        //check all possible values and see if any are repeated
        (1..puzzle.boundary).forEach { value ->
            //if count is > 1, it is an invalid row
            val occurrences = nodeList.filter { node ->
                //only look at the row, not every edge
                node.y == value
            }.count { node ->
                node.color == value
            }
            if (occurrences > 1) return true
        }
    }

    return false
}

internal fun columnsAreInvalid(puzzle: SudokuPuzzle): Boolean {
    (1..puzzle.boundary).forEach { column ->
        val nodeList: LinkedList<SudokuNode> = puzzle.graph[getHash(column, 1)]!!
        (1..puzzle.boundary).forEach { value ->
            val occurrences = nodeList.filter { node ->
                node.x == value
            }.count { node ->
                node.color == value
            }
            if (occurrences > 1) return true
        }
    }

    return false
}

internal fun superCliqueIsValid(superClique: LinkedList<SudokuNode>) {

}

/**
 * 1. Establish x and y values for one node in every subgrid
 * 2. get a list which represents that subgrid from the helper function
 * 3. check that list for repeats
 *
 *
 */
internal fun subgridsAreInvalid(puzzle: SudokuPuzzle): Boolean {
    val interval = puzzle.boundary.sqrt
    (1..interval).forEach { xIndex ->
        (1..interval).forEach { yIndex ->
            (1..puzzle.boundary).forEach { value ->
                val occurrences = getNodesBySubgrid(puzzle.graph,
                        xIndex * interval,
                        yIndex * interval,
                        puzzle.boundary
                ).count { node ->
                    node.color == value
                }
                if (occurrences > 1) return true
            }
        }
    }

    return false
}


internal fun getNodesByColumn(graph: LinkedHashMap<Int,
        LinkedList<SudokuNode>>, x: Int): List<SudokuNode> {
    val edgeList = mutableListOf<SudokuNode>()
    graph.values.filter {
        it.first.x == x
    }.forEach {
        edgeList.add(it.first)
    }
    return edgeList
}

internal fun getNodesByRow(graph: LinkedHashMap<Int,
        LinkedList<SudokuNode>>, y: Int): List<SudokuNode> {
    val edgeList = mutableListOf<SudokuNode>()
    graph.values.filter { it.first.y == y }.forEach { edgeList.add(it.first) }
    return edgeList
}

/**
 * Calculate the range of allowable x and y values for the given subgrid
 * Traverse for nodes which are within that range:
 * 1. Get the upper bound on the intervals for the x and y values
 * 2. iterate through lower bounds to upper bounds, selecting for any node within that
 * interval
 * 3. append each time
 *
 * @param x
 * @param y
 *
 */
internal fun getNodesBySubgrid(graph: LinkedHashMap<Int,
        LinkedList<SudokuNode>>, x: Int, y: Int, boundary: Int): List<SudokuNode> {
    val edgeList = mutableListOf<SudokuNode>()
    val iMaxX = getIntervalMax(boundary, x)
    val iMaxY = getIntervalMax(boundary, y)

    ((iMaxX - boundary.sqrt) + 1..iMaxX).forEach { xIndex ->
        ((iMaxY - boundary.sqrt) + 1..iMaxY).forEach { yIndex ->
            edgeList.add(
                    graph[getHash(xIndex, yIndex)]!!.first
            )
        }
    }

    return edgeList
}



/**
 * Calculate the upper bound of each interval, and establish which upper bound defines
 * the subgrid within which the target sits
 */
internal fun getIntervalMax(boundary: Int, target: Int): Int {
    var intervalMax = 0
    val interval = boundary.sqrt

    (1..interval).forEach { index ->
        //Is the target within rage of the upper and lower bound of this interval
        if (    //check against upper bound
                interval * index >= target &&
                //check against lower bound
                target > (interval * index - interval)
        ) {
            intervalMax = index * interval
            return@forEach
        }
    }
    return intervalMax
}

internal fun allSquaresAreNotEmpty(puzzle: SudokuPuzzle): Boolean {
    puzzle.graph.values.forEach {
        if (it[0].color == 0) return true
    }
    return false
}

internal fun SudokuPuzzle.print() {
    var outputLine = ""
    (1..boundary).forEach { yIndex ->
        graph.values.filter { node ->
            node.first.y == yIndex
        }.forEach { node ->
            outputLine += node.first.color
            outputLine += " "
        }
        outputLine += "\n"
    }
    println(outputLine)
}
