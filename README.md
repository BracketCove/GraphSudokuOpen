Note: This document contains a whole bunch of brainstorming and should not be considered as 
descriptive of the approaches I used in the end. I included it so that you can see how I verbally
break down complex problems and ideas before writing them in code. This allows me to separate the
issues of solving the problems and implementing them in code; instead of doing both at the same 
time, which is less efficient for complex problems.


Minimal # of clues by boundary (size):
4 (16) - 4 (25%)
9 (81) - 17 (21%)
16 (256) - 55 (21%)
25 (625) - 151 (24%)

Based on the above numbers, it seems reasonable to start by assuming that the number of clues
in an n sized sudoku out to be 25% of the total number of squares.

#Sudoku Solving Algorithm:
I am of the opinion that simply assigning values randomly, checking them against the rules of the
game, and implementing backtracking in the case of a non-terminating loop, will be insufficient for
larger sizes of puzzles, with respect to runtime. 

My reasoning is thus:
Suppose we select a zero (uncolored) node in a large graph (say a 9, but more so a 16 or 25 boundary 
sudoku puzzle). By checking the adjacency list of this node, we can determine the degree of certainty
with which a number (color) ought to be assigned to that particular node.

*Example 1:*
Upon checking the adjacency list of a zero node, we see that there is only one possible value that 
can be assigned to this particular node, that does not violate the rules of Sudoku. Assuming that 
the Sudoku board is valid, we can say this value may be assigned with the maximal degree of 
'certainty'; which is to say that it is the only appropriate value we can logically infer.

*Example 2:*
Upon checking the adjacency list of a zero node, we see that every edge points to another zero node.
This means that every possible value we can assign is equally likely to be correct (or wrong for
the cynically minded). Therefore, we can say that any assignment here is of minimal degree of
certainty, and we are relying on brute force, backtracking, and the divine grace of RNGesus; 
hallowed be thy System.currentTimeMillis().

Since my algorithms are meant to be configurable for dynamically sized (though symmetrical)
Sudoku puzzles, we must also take into account the boundary of the puzzle. In a puzzle with a small
boundary, the algorithm will take *minimal time* to increase the certainty of each assignment, 
whereas it will take quite a while for the algorithm to be making certain assignments in a boundary
25 puzzle. 

*Therefore I suppose that* the algorithm should be more and more careful about the degree to which
it is making an assignment, relative to the density of valid colored nodes. I imagine something
similar to what are called "Nice Values" in operating systems, where the algorithm will be more or
less choosy about making an assignment relative to the density of colored nodes, but also with 
respect to the number of attempted assignments.

Algorithm:
val assignments: LinkedList<SudokuNode>
var assignmentAttempts: Int
var backtrack: Int
var niceValue: Int
val initialGraph: HashMap<Int, LinkedList<SudokuNode>>
val boundary: Int
var newGraph: HashMap<Int, LinkedList<SudokuNode>>
val uncoloredNodes: LinkedList<SudokuNode>

General steps
1. Build uncoloredNodes by iterating through the initialGraph and selecting for nodes which are not
colored
2. Until uncoloredNodes.size == 0, Randomly select a node from uncoloredNodes
3. Establish a list of values for this node which are valid
4. (a) If the length of this list is greater than the niceValue
 * if niceValue < boundary, increment niceValue else increment assignmentAttempts
 * goto step 2
4. (b) If the length of this list is equal to or less than the niceValue:
 * randomly select a value from possible values list
 * assign that value to the selected node 
 * decrement the niceValue (unless it is already equal to 1)
 * add node to assignments list
 * remove node from uncoloredNodes list
 * goto step 2
 
 Backtracking:
 There are a few things to consider when it comes to backtracking. Firstly, if we do not make an
 assignment because the niceValue is too low, then this does not indicate that the algorithm should
 be concerned about backtracking just yet. Where we start to get worried is where maximal niceValue 
 does not lead to any randomly selected uncolored nodes being assigned. This would indicate that the
 graph cannot be solved in a logical fashion, and some kind of error has been made.
 
I will represent Backtracking with an int that represents three incremental steps:
0 - no need to backtrack
1 - assignmentAttempts has reached a value indicative that partial backtracking is necessary. For 
partial backtracking, we take a number of nodes from assignments, set them back to 0, and add them
back into uncoloredNodes
2 - having previously been incremented to stage 1, our partial backtracking has apparently failed,
and we nuke everything and start fresh from initialGraph
 
Nice Value:
The nice value must be proportionate to the boundary, and it should also start off with a value 
which makes sense. Further investigation is required, but we can begin with half of the boundary. 

#Unsolving And Difficulty
My first impression of writing the unsolving algorithm, which is meant to bring the solved sudoku
to a state where it is playable, that is to say that we uncolor a given number of nodes based on
the user's desired difficulty, is that there are two primary metrics:
1. The density of colored nodes
2. The number of certain choices (options.size == 1) for each node, based on what exists in the
clique (subgraph) of that node.

*Density of colored nodes from web generated sudoku for 9x9:*
Easy:
38, 38, 38, 38, 38 (47% colored)
Medium:
30, 30, 30, 30 (37% colored)
Hard:
25, 25, 25, 25 (30% colored)

*Density for 16x16:*
120 (47%)
95 (37%)
77 (30%)

*Density for 4x4:*
8
6
4


It seems as though a good approximation for the difficulty of a sudoku puzzle is indeed the density,
but what remains uncertain is whether or not 