package com.bracketcove.graphsudoku.domain

enum class Difficulty(val modifier:Double) {
    EASY(0.50),
    MEDIUM(0.44),
    HARD(0.38)
}