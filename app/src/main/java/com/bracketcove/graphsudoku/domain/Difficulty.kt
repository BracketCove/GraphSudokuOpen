package com.bracketcove.graphsudoku.domain

enum class Difficulty(val modifier:Double) {
    EASY(0.48),
    MEDIUM(0.38),
    HARD(0.30)
}