# Project Overview

Graph Sudoku is an application which was created with three goals in mind:
- Teach myself Jetpack Compose
- Teach myself Graph Datastructure & Algorithms (Directed Colored Graph)
- Build a simple and fun app

It uses my general purpose software architecture (model-view-nobody-gives-a-****) which is basically just applied separation of concerns.

**If you learned something from this repo, please do me a favour and [download the free app](https://play.google.com/store/apps/details?id=com.bracketcove.graphsudoku).**

# DS & Algos

The algorithms in here were written by me. I do not learn well at all from textbooks, so apart from spending a week trying to understand what an Adjacency List was, everything came from my head.

The only part that I am particularly proud of, is the Sudoku Solver algorithm. In order to make my algorithm more efficient, I decided to borrow a concept I learned from studying UNIX operating systems: Nice Values. What this means, is that as the algorithm attempts to allocate numbers to a puzzle in order to solve it, it will become more or less picky based on such allocations.  It took some time to tweak the values properly, but the end result can be summarized with the following benchmarks for building 101 puzzles:
**First benchmarks (101 puzzles)**:  
2.423313576E9 (4 m 3 s 979 ms to completion)  
2.222165776E9 (3 m 42 s 682 ms to completion)  
2.002508687E9 (3 m 20 s 624 ms ...)

**Second benchmarks** after refactoring seed algorithm:  (101 puzzles)  
3.526342681E9 (6 m 1 s 89 ms)  
3.024547185E9 (5 m 4 s 971 ms)

**Third Benchmarks** testing with and without nice values (10 puzzles)  
With:  
3.05801502E8  
6.14246012E8  
3.71489082E8

Without:  
Did not complete even after 10 minutes

**Fourth benchmarks**, niceValue may not go higher than boundary/2 (101 puzzles)  
3.639675188E9 (6 m 4 s 229 ms)

**Fifth benchmarks** niceValue only adjusted after a fairly comprehensive search (boundary *
boundary) for a suitable allocation 101 puzzles:

9 * 9:  
3774511.0 (480 ms)  
3482333.0 (456 ms)  
3840088.0 (468 ms)  
3813932.0 (469 ms)  
3169410.0 (453 ms)  
3908975.0 (484 ms)

16 * 16 (all previous benchmarks were for 9 * 9):  
9.02626914E8 (1 m 31 s 45 ms)  
7.75323967E8 (1 m 20 s 155 ms)  
7.06454975E8 (1 m 11 s 838 ms)

