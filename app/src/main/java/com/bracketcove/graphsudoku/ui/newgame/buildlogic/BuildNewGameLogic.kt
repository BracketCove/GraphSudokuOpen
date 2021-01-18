package com.bracketcove.graphsudoku.ui.newgame.buildlogic

import android.content.Context
import androidx.datastore.createDataStore
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
    val settingsDataStore = context.createDataStore(
        fileName = "game_settings.pb",
        serializer = GameSettingsSerializer
    )

    val statisticsDataStore = context.createDataStore(
        fileName = "user_statistics.pb",
        serializer = StatisticsSerializer
    )

    return NewGameLogic(
        container,
        viewModel,
        GameRepositoryImpl(
            LocalGameStorageImpl(context.filesDir.path),
            LocalSettingsStorageImpl(settingsDataStore)
        ),
        LocalStatisticsRepositoryImpl(
            statisticsDataStore
        ),
        ProductionDispatcherProvider
    )
}