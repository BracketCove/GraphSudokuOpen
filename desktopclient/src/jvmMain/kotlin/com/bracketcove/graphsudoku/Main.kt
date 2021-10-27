package com.bracketcove.graphsudoku

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.bracketcove.graphsudoku.domain.IGameRepository
import com.bracketcove.graphsudoku.persistence.DatabaseDriverFactory
import com.bracketcove.graphsudoku.persistence.GameFileStorage
import com.bracketcove.graphsudoku.persistence.GameRepositoryImpl
import com.bracketcove.graphsudoku.persistence.UserDataStorage
import com.bracketcove.graphsudoku.ui.ActiveGameContainer
import com.bracketcove.graphsudoku.ui.GraphSudokuTheme
import com.bracketcove.graphsudoku.ui.NewGameContainer
import com.bracketcove.graphsudoku.ui.STR_APP_NAME
import com.bracketcove.graphsudoku.ui.activegame.ActiveGameScreen
import com.bracketcove.graphsudoku.ui.activegame.ActiveGameViewModel
import com.bracketcove.graphsudoku.ui.newgame.NewGameScreen
import com.bracketcove.graphsudoku.ui.newgame.NewGameViewModel

import java.awt.SystemColor.window
import java.awt.Toolkit
import java.io.File


fun main() = application {

    var screenState by remember {
        mutableStateOf(
            ScreenState.ACTIVE_GAME
        )
    }

    val gameRepo = buildGameRepo()

    val screenSize = Toolkit.getDefaultToolkit().screenSize

    Window(
        state = rememberWindowState(
            width = (screenSize.width*.40).dp,
            height = (screenSize.height*.95).dp,
            position = WindowPosition(
                y = 0.dp,
                x = 0.dp
            )
        ),
        title = STR_APP_NAME,
        resizable = false,
        onCloseRequest = ::exitApplication,
    ) {
        when (screenState) {
            ScreenState.ACTIVE_GAME -> {
                val vm = ActiveGameViewModel()

                val container = ActiveGameContainer { newScreenState ->
                    screenState = newScreenState
                }

                container.start(
                    vm,
                    gameRepo
                )

                GraphSudokuTheme(darkTheme = true) {
                    ActiveGameScreen(
                        container.logic::onEvent,
                        vm
                    )
                }
            }

            ScreenState.NEW_GAME -> {
                val vm = NewGameViewModel()
                val container = NewGameContainer { newScreenState ->
                    screenState = newScreenState
                }
                container.start(
                    vm,
                    gameRepo
                )

                GraphSudokuTheme(darkTheme = true) {
                    NewGameScreen(
                        container.logic::onEvent,
                        vm
                    )
                }

            }
        }
    }
}

private fun buildGameRepo(): IGameRepository {
    return GameRepositoryImpl(
        GameFileStorage(
            System.getProperty("java.io.tmpdir")
        ),
        UserDataStorage(
            DatabaseDriverFactory()
        )
    )
}

enum class ScreenState {
    ACTIVE_GAME,
    NEW_GAME
}