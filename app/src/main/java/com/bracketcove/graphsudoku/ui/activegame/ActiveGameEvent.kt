package com.bracketcove.graphsudoku.ui.activegame

/**
 * Represents user interactions of a given feature
 */
sealed class ActiveGameEvent {
    data class OnInput(val input: Int) : ActiveGameEvent()
    data class OnTileFocused(val x: Int, val y: Int) : ActiveGameEvent()
    object OnNewGameClicked : ActiveGameEvent()
    object OnStart : ActiveGameEvent()
    object OnStop : ActiveGameEvent()
}
