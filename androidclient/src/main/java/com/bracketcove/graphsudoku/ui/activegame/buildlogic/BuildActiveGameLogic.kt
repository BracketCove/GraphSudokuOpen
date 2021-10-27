package com.bracketcove.graphsudoku.ui.activegame.buildlogic

import com.bracketcove.graphsudoku.persistence.DatabaseDriverFactory
import com.bracketcove.graphsudoku.persistence.GameFileStorage
import com.bracketcove.graphsudoku.persistence.GameRepositoryImpl
import com.bracketcove.graphsudoku.persistence.UserDataStorage
import com.bracketcove.graphsudoku.ui.activegame.ActiveGameActivity
import com.bracketcove.graphsudoku.ui.activegame.ActiveGameLogic
import com.bracketcove.graphsudoku.ui.activegame.ActiveGameViewModel
import com.bracketcove.graphsudoku.ui.activegame.IActiveGameContainer

internal fun ActiveGameActivity.buildActiveGameLogic(
    viewModel: ActiveGameViewModel
): ActiveGameLogic {
    return ActiveGameLogic(
        this,
        viewModel,
        GameRepositoryImpl(
            GameFileStorage(this.filesDir.path),
            UserDataStorage(DatabaseDriverFactory(this))
        ),
    )
}