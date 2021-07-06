package com.bracketcove.graphsudoku

import com.bracketcove.graphsudoku.computationlogic.*
import com.bracketcove.graphsudoku.domain.*
import com.bracketcove.graphsudoku.domain.UserStatistics
import com.bracketcove.graphsudoku.ui.activegame.ActiveGameContainer
import com.bracketcove.graphsudoku.ui.activegame.ActiveGameLogic
import com.bracketcove.graphsudoku.ui.newgame.NewGameLogic
import org.junit.Test
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GraphSudokuAlgorithmTests {


    /**
     * Notes:
     * As the number of givens is reduced, it because very difficult to solve a Sudoku puzzle
     * using the Basic Solver.
     *
     * Benchmarks: MEDIUM USES ADVANCED ALGO
     * Easy 9x9 (.48)
     * 2 s 313 ms
     * Medium 9x9 (.38)
     * 12 s 943 ms
     *
     * Easy 9x9 (.50)
     * 1 s 293 ms
     * Medium 9x9 (.44)
     * 2 s 766 ms
     * 2 s 710 ms
     *
     * Hard 9x9 (.36)
     * 18s 451 ms
     * 18s 944 ms
     *
     * Hard 9x9 (.38)
     * 15s
     * 11s 944 ms
     * 14s
     *
     * Hard 9x9 (.36)  SINGLE PUZZLE ONLY
     * 444 ms
     *
     * Benchmarks: MEDIUM USES BASIC ALGO
     * Medium 9x9 (.44)
     * 4.5s
     * 5.1s
     * 3.5s
     *
     */
    @Test
    fun unsolverTest(){
        val list = mutableListOf<SudokuPuzzle>()
            buildNewSudoku(9, Difficulty.MEDIUM)

    }

    @Test
    fun getPossibleTestValues() {
        val puzzle = SudokuPuzzle(4, Difficulty.EASY)

        //reset values
        puzzle.graph.values.forEach { it.first.color = 0 }

        puzzle.graph[getHash(2, 1)]!!.first.color = 2
        puzzle.graph[getHash(2, 2)]!!.first.color = 1
        puzzle.graph[getHash(3, 1)]!!.first.color = 1
        puzzle.graph[getHash(4, 2)]!!.first.color = 3
        puzzle.graph[getHash(1, 4)]!!.first.color = 2
        puzzle.graph[getHash(3, 3)]!!.first.color = 4

        println(puzzle.print())

    }

    @Test
    fun testSuperCliqueCountOccurences() {
        val puzzle = SudokuPuzzle(4, Difficulty.EASY)
        println(puzzle.print())

        val superClique = getSuperClique(puzzle.graph.values.first().first, puzzle)
        val boundary = puzzle.boundary
        val key = puzzle.graph.values.first().first
        val iMaxX = getIntervalMax(boundary, key.x)
        val iMaxY = getIntervalMax(boundary, key.y)


        assert(superClique.filter { node ->
            when {
                (node.x == key.x && node.y != key.y) -> true
                (node.x != key.x && node.y == key.y) -> true
                (
                        iMaxX == getIntervalMax(boundary, node.x) &&
                                iMaxY == getIntervalMax(boundary, node.y)
                        ) -> true
                else -> false
            }
        }.count().also { println(it) } == 8)

    }

    @Test
    fun difficultyByTechniqueTests() {
        val list = mutableListOf<SudokuPuzzle>()
        (1..100).forEach {
            list.add(buildNewSudoku(9, Difficulty.EASY))
        }

        println(
            list.count {
                isBasic(it.copy())
            }
        )

        println(
            list.count {
                isAdvanced(it.copy())
            }
        )


    }

    @Test
    fun difficultyTests() {
        val fourGraphEasy = buildNewSudoku(4, Difficulty.EASY).graph
        var coloredNodesFour = fourGraphEasy.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesFour == 8)

        val fourGraphMed = buildNewSudoku(4, Difficulty.MEDIUM).graph
        coloredNodesFour = fourGraphMed.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesFour == 7)

        val fourGraphHard = buildNewSudoku(4, Difficulty.HARD).graph
        coloredNodesFour = fourGraphHard.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesFour == 5)


        val nineGraphEasy = buildNewSudoku(9, Difficulty.EASY).graph
        var coloredNodesNine = nineGraphEasy.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesNine == 38)

        val nineGraphMed = buildNewSudoku(9, Difficulty.MEDIUM).graph
        coloredNodesNine = nineGraphMed.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesNine == 30)

        val nineGraphHard = buildNewSudoku(9, Difficulty.HARD).graph
        coloredNodesNine = nineGraphHard.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesNine == 24)


        val sixteenGraphEasy = buildNewSudoku(16, Difficulty.EASY).graph
        var coloredNodesSixteen =
            sixteenGraphEasy.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesSixteen == 122)

        val sixteenGraphMedium = buildNewSudoku(16, Difficulty.MEDIUM).graph
        coloredNodesSixteen =
            sixteenGraphMedium.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesSixteen == 97)

        val sixteenGraphHard = buildNewSudoku(16, Difficulty.HARD).graph
        coloredNodesSixteen = sixteenGraphHard.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesSixteen == 76)
    }

    @Test
    fun rangeTest() {
        (1..9).forEach {
            println(it)
        }
    }

    /**
     * Trying to figure out what a good minumum nice value is based on the seed algorithm
     * Averages consistently:
     * boundary 4 = 2
     * boundary 9 = 6
     * boundary 16 = 12
     * boundary 25 = 19
     */
    @Test
    fun minumumCliqueNiceValueTest() {

        println("first")
        var average = 0

        val fourGraph = SudokuPuzzle(4, Difficulty.MEDIUM)
        fourGraph.graph.values.forEach {
            average += getPossibleValues(it, 4).size
        }

        println(average / 16)

        average = 0

        val nineGraph = SudokuPuzzle(9, Difficulty.MEDIUM)
        nineGraph.graph.values.forEach {
            average += getPossibleValues(it, 9).size
        }
        println(average / 81)

        average = 0

        val sixteenGraph = SudokuPuzzle(16, Difficulty.MEDIUM)
        sixteenGraph.graph.values.forEach {
            average += getPossibleValues(it, 16).size
        }
        println(average / 256)

        average = 0

        val twentyFive = SudokuPuzzle(25, Difficulty.MEDIUM)
        twentyFive.graph.values.forEach {
            average += getPossibleValues(it, 25).size
        }
        println(average / 625)
    }


    @Test
    fun linkedHashMapCopyTest() {
        val fourGraph = SudokuPuzzle(4, Difficulty.MEDIUM)

        val newMap = LinkedHashMap(fourGraph.graph)

        println("blah")

    }

    @Test
    fun verifySolverAlgorithm() {

        val fourGraph = SudokuPuzzle(4, Difficulty.MEDIUM)

        fourGraph.graph.values.forEach {
            assert(it.first.color != 0)
        }


        val nineGraph = SudokuPuzzle(9, Difficulty.MEDIUM)

        nineGraph.graph.values.forEach {
            assert(it.first.color != 0)
        }

//        val sixteenGraph = SudokuPuzzle(16)
//
//        sixteenGraph.graph.values.forEach {
//            assert(it.first.color != 0)
//        }
    }

    /**
     * Note: benchmarks for the JVM can be pretty flaky, but the goal here is to just get a general
     * sense of which tweaks I make to the algorithm actually speed it up, such as adjusting
     * niceValue or improving the seed algorithm.
     *
     * First benchmarks (101 puzzles):
     * 2.423313576E9 (4 m 3 s 979 ms to completion)
     * 2.222165776E9 (3 m 42 s 682 ms to completion)
     * 2.002508687E9 (3 m 20 s 624 ms ...)
     *
     * Second benchmarks after refactoring seed algorithm:  (101 puzzles)
     * 3.526342681E9 (6 m 1 s 89 ms)
     * 3.024547185E9 (5 m 4 s 971 ms)
     *
     * Third Benchmarks testing with and without nice values (10 puzzles)
     * With:
     * 3.05801502E8
     * 6.14246012E8
     * 3.71489082E8
     *
     * Without:
     * Did not complete even after 10 minutes
     *
     * Fourth benchmarks, niceValue may not go higher than boundary/2 (101 puzzles)
     * 3.639675188E9 (6 m 4 s 229 ms)
     *
     * Fifth benchmarks niceValue only adjusted after a fairly comprehensive search (boundary *
     * boundary) for a suitable allocation 101 puzzles:
     *
     * 9 * 9:
     * 3774511.0 (480 ms)
     * 3482333.0 (456 ms)
     * 3840088.0 (468 ms)
     * 3813932.0 (469 ms)
     * 3169410.0 (453 ms)
     * 3908975.0 (484 ms)
     *
     * 16 * 16 (all previous benchmarks were for 9 * 9):
     * 9.02626914E8 (1 m 31 s 45 ms)
     * 7.75323967E8 (1 m 20 s 155 ms)
     * 7.06454975E8 (1 m 11 s 838 ms)
     *
     *
     * (boundary cubed) 9 * 9:
     * (1 s 171 ms)
     * (1 s 171 ms)
     * (1 s 265 ms)
     *
     * (boundary only) 9 * 9:
     * (1 s 125 ms)
     * (1 s 422 ms)
     *
     * (partial backtrack only 1/4 of assignments.size) 9 * 9
     * (515 ms)
     * (485 ms)
     * (485 ms)
     * (484 ms)
     * (469 ms)
     *
     * */
    @Test
    fun solverBenchmarks() {
        //Run the code once to hopefully warm up the JIT
        SudokuPuzzle(9, Difficulty.EASY).graph.values.forEach {
            assert(it.first.color != 0)
        }

        (1..100).forEach {
            SudokuPuzzle(9, Difficulty.EASY)
        }

    }


    @Test
    fun verifyGraphSize() {
        val fourGraph = SudokuPuzzle(4, Difficulty.MEDIUM)
        val nineGraph = SudokuPuzzle(9, Difficulty.MEDIUM)
        val sixteenGraph = SudokuPuzzle(16, Difficulty.MEDIUM)

        assert(fourGraph.graph.size == 16)
        assert(nineGraph.graph.size == 81)
        assert(sixteenGraph.graph.size == 256)
    }

    /**
     * Prior to solving the game, the game will be seeded with a semi-random set of values. The
     * goal is not to provide a solveable game just yet; we are just trying to give the
     * graph solver something to start with. We can make this easier by moving laterally and vert-
     * ically for each color assignment, to ensure that the rules are not violated.

     *
     * NOTE: By unique, the random numbers may not be repeated, lest they violate the rules of the
     * game!
     */
    @Test
    fun testGraphColorSeed() {
        val fourGraph = buildNewSudoku(4, Difficulty.MEDIUM).graph
        val coloredNodesFour = fourGraph.entries.filter { it.value.first.color != 0 }.count()
        assert(coloredNodesFour == 3)
        assert(puzzleIsValid(SudokuPuzzle(4, Difficulty.MEDIUM, fourGraph)))

        val nineGraph = buildNewSudoku(9, Difficulty.MEDIUM).graph
        val coloredNodesNine = nineGraph.entries.filter { it.value.first.color != 0 }.count()
          assert(coloredNodesNine == 20)
        assert(puzzleIsValid(SudokuPuzzle(9, Difficulty.MEDIUM, nineGraph)))


        val sixteenGraph = buildNewSudoku(16, Difficulty.MEDIUM).graph
        val coloredNodesSixteen = sixteenGraph.entries.filter { it.value.first.color != 0 }.count()
               assert(coloredNodesSixteen == 64)
        assert(puzzleIsValid(SudokuPuzzle(16, Difficulty.MEDIUM, sixteenGraph)))

    }

    @Test
    fun validityTest() {
        assert(!rowsAreInvalid(SudokuPuzzle(4, Difficulty.MEDIUM)))
        assert(!rowsAreInvalid(SudokuPuzzle(9, Difficulty.MEDIUM)))
        assert(!rowsAreInvalid(SudokuPuzzle(16, Difficulty.MEDIUM)))
        assert(!rowsAreInvalid(SudokuPuzzle(25, Difficulty.MEDIUM)))

        assert(!columnsAreInvalid(SudokuPuzzle(4, Difficulty.MEDIUM)))
        assert(!columnsAreInvalid(SudokuPuzzle(9, Difficulty.MEDIUM)))
        assert(!columnsAreInvalid(SudokuPuzzle(16, Difficulty.MEDIUM)))
        assert(!columnsAreInvalid(SudokuPuzzle(25, Difficulty.MEDIUM)))

        assert(!subgridsAreInvalid(SudokuPuzzle(4, Difficulty.MEDIUM)))
        assert(!subgridsAreInvalid(SudokuPuzzle(9, Difficulty.MEDIUM)))
        assert(!subgridsAreInvalid(SudokuPuzzle(16, Difficulty.MEDIUM)))
        assert(!subgridsAreInvalid(SudokuPuzzle(25, Difficulty.MEDIUM)))

        assert(allSquaresAreNotEmpty(SudokuPuzzle(4, Difficulty.MEDIUM)))
        assert(allSquaresAreNotEmpty(SudokuPuzzle(9, Difficulty.MEDIUM)))
        assert(allSquaresAreNotEmpty(SudokuPuzzle(16, Difficulty.MEDIUM)))
        assert(allSquaresAreNotEmpty(SudokuPuzzle(25, Difficulty.MEDIUM)))
    }

    @Test
    fun testHash() {
        val first = SudokuNode(1, 4)

        assert(first.hashCode() == 1004)
    }

    @Test
    fun getIntervalMaxTest() {
        var boundary = 4
        var target = 1

        var iMax = getIntervalMax(boundary, target)

        assert(iMax == 2)

        boundary = 9
        target = 5

        iMax = getIntervalMax(boundary, target)

        assert(iMax == 6)

        boundary = 16
        target = 2

        iMax = getIntervalMax(boundary, target)

        assert(iMax == 4)
    }

    @Test
    fun mergeTest() {
        val firstList = LinkedList<SudokuNode>()
        firstList.add(SudokuNode(1, 1, 0))
        val secondList = LinkedList<SudokuNode>()

        secondList.add(
            SudokuNode(1, 1, 0)
        )

        secondList.add(
            SudokuNode(1, 2, 0)
        )

        secondList.add(
            SudokuNode(1, 3, 0)
        )

        secondList.add(
            SudokuNode(1, 3, 0)
        )

        secondList.add(
            SudokuNode(1, 4, 0)
        )

        secondList.add(
            SudokuNode(1, 1, 0)
        )

        secondList.add(
            SudokuNode(1, 2, 0)
        )

        secondList.add(
            SudokuNode(1, 3, 0)
        )

        secondList.add(
            SudokuNode(1, 3, 0)
        )

        secondList.add(
            SudokuNode(1, 4, 0)
        )


        firstList.mergeWithoutRepeats(secondList)


        assert(firstList.size == 4)
    }


    /**
     *
     */
    @Test
    fun verifyEdgesBuilt() {
        val fourGraph = SudokuPuzzle(4, Difficulty.MEDIUM)
        val nineGraph = SudokuPuzzle(9, Difficulty.MEDIUM)
        val sixteenGraph = SudokuPuzzle(16, Difficulty.MEDIUM)

        fourGraph.graph.forEach {
            assert(it.value.size == 8)
        }

        nineGraph.graph.forEach {
            assert(it.value.size == 21)
        }

        sixteenGraph.graph.forEach {
            assert(it.value.size == 40)
        }
    }
}