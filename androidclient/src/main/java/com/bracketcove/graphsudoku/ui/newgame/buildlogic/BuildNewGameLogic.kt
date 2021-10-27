package com.bracketcove.graphsudoku.ui.newgame.buildlogic

import com.bracketcove.graphsudoku.persistence.DatabaseDriverFactory
import com.bracketcove.graphsudoku.persistence.GameFileStorage
import com.bracketcove.graphsudoku.persistence.GameRepositoryImpl
import com.bracketcove.graphsudoku.persistence.UserDataStorage
import com.bracketcove.graphsudoku.ui.newgame.INewGameContainer
import com.bracketcove.graphsudoku.ui.newgame.NewGameActivity
import com.bracketcove.graphsudoku.ui.newgame.NewGameLogic
import com.bracketcove.graphsudoku.ui.newgame.NewGameViewModel

internal fun NewGameActivity.buildNewGameLogic(
    viewModel: NewGameViewModel
): NewGameLogic {
    return NewGameLogic(
        this,
        viewModel,
        GameRepositoryImpl(
            GameFileStorage(this.filesDir.path),
            UserDataStorage(DatabaseDriverFactory(this))
        )
    )
}