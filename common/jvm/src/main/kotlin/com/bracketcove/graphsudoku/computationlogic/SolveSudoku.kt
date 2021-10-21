package com.bracketcove.graphsudoku.computationlogic

import com.bracketcove.graphsudoku.domain.SudokuNode
import com.bracketcove.graphsudoku.domain.SudokuPuzzle
import com.bracketcove.graphsudoku.domain.getHash
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.random.Random


internal fun SudokuPuzzle.solve()
        : SudokuPuzzle {
    //nodes that have been assigned (not including nodes seeded from seedColors()
    val assignments = LinkedList<SudokuNode>()

    //keep track of failed assignment attempts to watch for infinite loops
    var assignmentAttempts = 0
    //Two stages of backtracking, partial is half the dataset, full is a complete restart
    var partialBacktrack = false

    var fullbacktrackCounter = 0

    //from 0 - boundary, represents how "picky" the algorithm is about assigning new values
    var niceValue: Int = (boundary / 2)

    //to avoid being too nice too soon
    var niceCounter = 0

    //work with a copy
    var newGraph = LinkedHashMap(this.graph)
    //all nodes which are of 0 value (uncolored)
    val uncoloredNodes = LinkedList<SudokuNode>()
    newGraph.values.filter { it.first.color == 0 }.forEach { uncoloredNodes.add(it.first) }

    while (uncoloredNodes.size > 0) {
        //backtracking
        if (assignmentAttempts > boundary * boundary && partialBacktrack) {
            //full backtrack
            assignments.forEach { node ->
                node.color = 0
                uncoloredNodes.add(node)
            }

            assignments.clear()

            assignmentAttempts = 0
            partialBacktrack = false
            fullbacktrackCounter++
        } else if (assignmentAttempts > boundary * boundary * boundary) {
            /* Partial Backtrack: take half of the nodes from assignments and:
            - reset them to 0
            - add them to uncoloredNodes
            - remove them from assignments

            Reset assignmentAttempts to 0 but leave backtrack
             */
            partialBacktrack = true
            assignments.takeLast(assignments.size / 2)
                .forEach { node ->
                    node.color = 0
                    uncoloredNodes.add(node)
                    assignments.remove(node)
                }

            assignmentAttempts = 0
        }

        //final backtracking stage
        if (fullbacktrackCounter == boundary * boundary) {

            newGraph = this.seedColors().graph
            uncoloredNodes.clear()
            newGraph.values.filter { it.first.color == 0 }.forEach { uncoloredNodes.add(it.first) }
            assignments.clear()
            fullbacktrackCounter = 0
            niceValue = (boundary / 2)
        }

        val node = uncoloredNodes[Random.nextInt(0, uncoloredNodes.size)]

        val options = getPossibleValues(newGraph[getHash(node.x, node.y)]!!, boundary)
        //     println(options.size.toString() + node.hashCode().toString())

        if (options.size == 0) assignmentAttempts++
        else if (options.size > niceValue) {
            niceCounter++
            if (niceCounter > boundary * boundary) {
                niceValue++
                niceCounter = 0
            }
        } else {
            val color = options[Random.nextInt(0, options.size)]
            node.color = color
            uncoloredNodes.remove(node)
            assignments.add(node)
            if (niceValue > 1) niceValue--
        }
    }

    this.graph.clear()
    this.graph.putAll(newGraph)
    return this
}

fun getPossibleValues(adjList: LinkedList<SudokuNode>, boundary: Int): List<Int> {
    val options = mutableListOf<Int>()
    (1..boundary).forEach {
        adjList.first.color = it

        val occurrences = adjList.count { node ->
            node.color == it
        }

        if (occurrences == 1) options.add(it)
    }

    //reset to 0
    adjList.first.color = 0
    return options
}

/**
 * This version accepts a Key in the event that we don't just want to match against the first
 * element in the list
 */
fun getPossibleValues(
    key: SudokuNode,
    adjList: LinkedList<SudokuNode>,
    boundary: Int
): List<Int> {
    val options = mutableListOf<Int>()

    val iMaxX = getIntervalMax(boundary, key.x)
    val iMaxY = getIntervalMax(boundary, key.y)

    (1..boundary).forEach {
        key.color = it

        val occurrences = adjList.filter { node ->
            when {
                (node.x == key.x && node.y != key.y) -> true
                (node.x != key.x && node.y == key.y) -> true
                (
                        iMaxX == getIntervalMax(boundary, node.x) &&
                                iMaxY == getIntervalMax(boundary, node.y)
                        ) -> true
                else -> false
            }
        }.count { node ->
            node.color == it
        }

        if (occurrences == 1) options.add(it)
    }

    //reset to 0
    key.color = 0
    return options
}

