package com.bracketcove.graphsudoku.ui.newgame

import com.bracketcove.graphsudoku.domain.DIFFICULTY

sealed class NewGameEvent {
    object OnStart: NewGameEvent()
    data class OnSizeChanged(val boundary: Int): NewGameEvent()
    data class OnDifficultyChanged(val diff: DIFFICULTY): NewGameEvent()
    object OnDonePressed: NewGameEvent()
}