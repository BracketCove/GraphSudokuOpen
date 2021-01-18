package com.bracketcove.graphsudoku.ui.activegame

import android.content.Context
import androidx.datastore.createDataStore
import com.bracketcove.graphsudoku.common.ProductionDispatcherProvider
import com.bracketcove.graphsudoku.persistence.*
import java.util.*


internal fun buildActiveGameLogic(
    container: ActiveGameContainer,
    viewModel: ActiveGameViewModel,
    context: Context
): ActiveGameLogic {
    val dataStore = context.createDataStore(
        fileName = "game_settings.pb",
        serializer = GameSettingsSerializer
    )

    val statisticsDataStore = context.createDataStore(
        fileName = "user_statistics.pb",
        serializer = StatisticsSerializer
    )

    return ActiveGameLogic(
        container,
        viewModel,
        GameRepositoryImpl(
            LocalGameStorageImpl(context.filesDir.path),
            LocalSettingsStorageImpl(dataStore)
        ),
        LocalStatisticsRepositoryImpl(
            statisticsDataStore
        ),
        ProductionDispatcherProvider
    )
}