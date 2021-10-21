package com.bracketcove.graphsudoku.computationlogic

import com.bracketcove.graphsudoku.domain.Difficulty
import com.bracketcove.graphsudoku.domain.SudokuNode
import com.bracketcove.graphsudoku.domain.SudokuPuzzle
import com.bracketcove.graphsudoku.domain.getHash
import java.util.*
import kotlin.random.Random


/**
 * Remove a certain number of "clues" from a sudoku puzzle, relative to the desired difficulty.
 *
 * Givens are the nodes which are left colored
 * "remove" refers to the nodes which are made uncolored relative to the difficulty
 */
internal fun SudokuPuzzle.unsolve(): SudokuPuzzle {
    var remove = ((boundary * boundary) - (boundary * boundary * difficulty.modifier)).toInt()

    //track allocations for easy backtracking. Used both to remove and add allocations back in
    val allocations = mutableListOf<SudokuNode>()


    var counter = 0

    while (counter <= remove) {
        var colored = true
        while (colored) {
            val randX = Random.nextInt(1, boundary + 1)
            val randY = Random.nextInt(1, boundary + 1)

            val node = this.graph[getHash(randX, randY)]!!.first



            if (node.color != 0) {
                allocations.add(
                    SudokuNode(
                        node.x,
                        node.y,
                        node.color,
                        node.readOnly
                    )
                )

                node.color = 0
                colored = false
                counter++
            }
        }
    }

    //work on a copy
    return when (determineDifficulty(this)) {
        SolvingStrategy.BASIC -> if (this.difficulty == Difficulty.EASY || this.difficulty == Difficulty.MEDIUM) this.apply {
            //success
            allocations.forEach { node ->
                this.graph[node.hashCode()]!!.first.color = 0
                this.graph[node.hashCode()]!!.first.readOnly = false
            }
        } else {        //failure
            this.apply {
                allocations.forEach { node ->
                    this.graph[node.hashCode()]!!.first.color = node.color
                }
            }.unsolve()
        }
        SolvingStrategy.ADVANCED -> if (this.difficulty == Difficulty.HARD )
        //success
            this.apply {
                allocations.forEach { node ->
                    this.graph[node.hashCode()]!!.first.color = 0
                    this.graph[node.hashCode()]!!.first.readOnly = false
                }
            } else
        //failure
            this.apply {
                allocations.forEach { node ->
                    this.graph[node.hashCode()]!!.first.color = node.color
                }
            }.unsolve()
        //failure
        else -> this.apply {
            allocations.forEach { node ->
                this.graph[node.hashCode()]!!.first.color = node.color
            }
        }.unsolve()
    }
}

/**
 * This function is called with the initial desired difficulty of the puzzle.
 * When it finally returns, we check to see if it matches.
 *
 * NOTE: WORK ON A COPY OF PUZZLE!!!
 */
internal fun determineDifficulty(
    puzzle: SudokuPuzzle
): SolvingStrategy {
    val basicSolve = isBasic(
        puzzle
    )
    val advancedSolve = isAdvanced(
        puzzle
    )

    //if puzzle is no longer solveable, we return the current strategy
    if (basicSolve) return SolvingStrategy.BASIC
    else if (advancedSolve) return SolvingStrategy.ADVANCED
    else {
        puzzle.print()
        return SolvingStrategy.UNSOLVABLE
    }
}

/**
 * Basic - An basic Sudoku can be solved using a single process:
 * For each uncolored node, until entire puzzle is traversed:
 * - check if at least one node can be colored by looking at it's adjacency list
 * - if yes, color that node and start again from the beginning in case, that coloring has also
 * allowed another node to be colored follow basic rules
 * - if entire puzzle is traversed without a coloring, we can assume that basic rules are not enough
 * to solve this puzzle
 * */
internal fun isBasic(puzzle: SudokuPuzzle): Boolean {
    var solveable = true

    while (solveable) {
        solveable = false

        puzzle.graph.values.forEach {
            //if it works at least once
            if (basicSolver(it, puzzle.boundary)) solveable = true
        }

        if (puzzleIsComplete(puzzle)) return true
    }

    return solveable
}

/**
 * A clique is DS technobabble for a sub-graph. In this case it represents all nodes in a
 * corresponding column, row, and sub-grid
 */
internal fun basicSolver(clique: LinkedList<SudokuNode>, boundary: Int): Boolean {
    if (clique.first.color == 0) {
        val options = getPossibleValues(clique, boundary)

        if (options.size == 1) {
            clique.first.color = options.first()
            //if a single node can be colored, assume it is still solvable for now
            return true
        }
    }

    return false
}

/**
 * Assume that the puzzle may have been partially solved by the basic algorithm.
 *
 * 1. If a node has 1 possible value, use the easy solver on it (no need to do extra work)
 * 2. If a node has 2 possible values, attempt to locate another node in that clique where:
 *      - It also has 2 possible values
 *      - The values are the same as the first node
 * 3. Apply those values in the two possible configurations:
 *      - One config results in valid sudoku, one does not (a)
 *      - Both configs are valid means we don't have enough info to assert anything
 *      - Both configs false would mean an error
 * 4a. fill numbers in
 * 4b. undo changes and skip to next node
 */
internal fun isAdvanced(puzzle: SudokuPuzzle): Boolean {
    var solveable = true

    while (solveable) {
        solveable = false

        puzzle.graph.values.filter { adjList -> adjList.first.color == 0 }.forEach {
            //attempt with easy solver
            if (basicSolver(it, puzzle.boundary)) solveable = true
            else {
                val superClique: LinkedList<SudokuNode> = getSuperClique(it.first, puzzle)
                if (advancedSolver(puzzle, superClique, puzzle.boundary)) solveable = true
            }
        }

        if (puzzleIsComplete(puzzle)) return true
    }

    return solveable
}

/**
 * 2. If a node has 2 possible values, attempt to locate another node in that clique where:
 *      - It also has 2 possible values
 *      - The values are the same as the first node
 * 3. Apply those values in the two possible configurations:
 *      - One config results in valid sudoku, one does not (a)
 *      - Both configs are valid means we don't have enough info to assert anything
 *      - Both configs false would mean an error
 *
 *      Instead of testing for one single matching pair, try testing all potential matches
 */
fun advancedSolver(
    puzzle: SudokuPuzzle,
    superClique: LinkedList<SudokuNode>,
    boundary: Int
): Boolean {
    val firstNode = superClique.first()

    val firstOptions = getPossibleValues(firstNode, superClique, boundary)

    if (firstOptions.size != 2) return false

    val pairs = mutableListOf<SudokuNode>()

    superClique
        .forEach { node ->
            if (node.color == 0 && node != firstNode) {
                val secondOptions = getPossibleValues(node, superClique, boundary)
                if (secondOptions.size == 2 && areSameOptions(firstOptions, secondOptions)) {
                    pairs.add(node)
                }
            }
        }

    //no match found
    if (pairs.size == 0) return false

    if (
        puzzle.graph.values.count {
            it[0].color == 0
        } == firstOptions.size * 2
    ) return false

    pairs.forEach { pairNode ->
        //if a valid match is made, return
        if (
            testPair(
                firstOptions,
                firstNode,
                pairNode,
                puzzle
            )
        ) return true
    }

    return false
}

fun testPair(
    options: List<Int>,
    firstNode: SudokuNode,
    pairNode: SudokuNode,
    puzzle: SudokuPuzzle
): Boolean {
    firstNode.color = options[0]
    pairNode.color = options[1]

    val firstConfigIsValid = puzzleIsValid(puzzle)

    firstNode.color = options[1]
    pairNode.color = options[0]

    val secondConfigIsValid = puzzleIsValid(puzzle)

    if (firstConfigIsValid && !secondConfigIsValid) {
        firstNode.color = options[0]
        pairNode.color = options[1]

        return true
    }
    else if (!firstConfigIsValid && secondConfigIsValid) {
        firstNode.color = options[1]
        pairNode.color = options[0]

        return true
    }
    else if (firstConfigIsValid && secondConfigIsValid) {
        //50% of the time, this code works 100% of the time.
        firstNode.color = options[1]
        pairNode.color = options[0]
        return true
    }
    else {
        firstNode.color = 0
        pairNode.color = 0
        return false
    }
}

fun areSameOptions(firstOptions: List<Int>, secondOptions: List<Int>): Boolean {
    firstOptions.forEach {
        if (!secondOptions.contains(it)) return false
    }

    return true
}

/**
 * A clique is a sub-graph (some part of the entire Graph DS that makes up the whole puzzle).
 * I refer to a superClique as not just a corresponding row, column, and sub-grid, but also any
 * node which shares an x or y interval of the incoming node. For example:
 * [0][2][0][1]
 * [0][1][2][0]
 * [2][0][0][0]
 * [1][0][0][0]
 *
 * If I select for node at [x1, y1], all nodes in the top-right and bottom left sub-grid may
 * possibly possess information which may be used to infer a value for the selected node.
 *
 * Algorithm:
 * 1. Establish the interval of x and y
 * 2. Add all nodes in both the x and y intervals to a map
 */
internal fun getSuperClique(first: SudokuNode, puzzle: SudokuPuzzle): LinkedList<SudokuNode> {
    val superClique = LinkedList<SudokuNode>()
    superClique.add(first)

    val iMaxX = getIntervalMax(puzzle.boundary, first.x)
    val iMaxY = getIntervalMax(puzzle.boundary, first.y)

    //get nodes by x interval:
    ((iMaxX - puzzle.boundary.sqrt) + 1..iMaxX).forEach { xIndex ->
        (1..puzzle.boundary).forEach { yIndex ->
            val node = puzzle.graph[getHash(xIndex, yIndex)]!!.first
            if (!superClique.contains(node)) superClique.add(node)
        }
    }

    //get nodes by y interval:
    ((iMaxY - puzzle.boundary.sqrt) + 1..iMaxY).forEach { yIndex ->
        (1..puzzle.boundary).forEach { xIndex ->
            val node = puzzle.graph[getHash(xIndex, yIndex)]!!.first
            if (!superClique.contains(node)) superClique.add(node)
        }
    }

    return superClique
}




enum class SolvingStrategy {
    BASIC,
    ADVANCED,
    UNSOLVABLE
}







