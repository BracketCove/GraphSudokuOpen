package com.bracketcove.graphsudoku.ui.newgame.buildlogic

import android.content.Context
import com.bracketcove.graphsudoku.common.ProductionDispatcherProvider
import com.bracketcove.graphsudoku.persistence.*
import com.bracketcove.graphsudoku.ui.newgame.NewGameContainer
import com.bracketcove.graphsudoku.ui.newgame.NewGameLogic
import com.bracketcove.graphsudoku.ui.newgame.NewGameViewModel

internal fun buildNewGameLogic(
    container: NewGameContainer,
    viewModel: NewGameViewModel,
    context: Context
): NewGameLogic {
    return NewGameLogic(
        container,
        viewModel,
        GameRepositoryImpl(
            LocalGameStorageImpl(context.filesDir.path),
            LocalSettingsStorageImpl(context.settingsDataStore)
        ),
        LocalStatisticsStorageImpl(
            context.statsDataStore
        ),
        ProductionDispatcherProvider
    )
}