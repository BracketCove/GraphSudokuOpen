# Project Overview

###Note:
1. Please see the multiplatform branch for the latest version.
2. This project has been made


Graph Sudoku is an application which was created with three goals in mind:
- Teach myself Jetpack Compose
- Teach myself Graph Datastructures & Algorithms (Directed Colored Graph)
- Build a simple and fun app

**If you learned something from this repo, please do me a favour and [download the free app](https://play.google.com/store/apps/details?id=com.bracketcove.graphsudoku).**

#Features
###Active Game:
####Android
[Active Game Android Screenshot](android_active_game.png)
####Desktop
[Active Game Desktop Screenshot](desktop_active_game.png)


###Create New Game:
####Android
[New Game Android Screenshot](android_new_game.png)

####Desktop
[New Game Desktop Screenshot](desktop_new_game.png)


#Architecture:
I follow my own architecture which consists of these things mainly:
- Front end platform code (mostly UI stuff): Shared Composables
- Computation Logic for Sudoku Games: Pure Kotlin STDLib
- Front end presentation logic: Shared ViewModels and Presentation Logic, Kotlin + Coroutines
- Back End Business Logic (back end stuff that doesn't know about IO devices): Kotlin suspend functions
- Back end IO device specific stuff: GameData stored in filestorage, other data in SQLDelight, Kotlin + Coroutines
- GUI Feature Containers: Activity for Android, Simple Class for Desktop

**This roughly translates into Container, ViewModel, Screen (View), Logic for each feature.** 
I have only a single backend repository. This is due to the scale of the app being quite small, and the 
process necessary for creating a new Sudoku Puzzle and storing settings/user records being tightly
coupled together. 



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

