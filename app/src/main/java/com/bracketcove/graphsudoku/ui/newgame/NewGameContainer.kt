package com.bracketcove.graphsudoku.ui.newgame

import com.bracketcove.graphsudoku.domain.Messages

interface NewGameContainer {
    fun showMessage(message: Messages)
    fun onDoneClick()
}