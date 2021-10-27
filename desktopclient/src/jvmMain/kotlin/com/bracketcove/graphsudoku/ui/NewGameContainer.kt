package com.bracketcove.graphsudoku.ui

import com.bracketcove.graphsudoku.DispatcherProvider
import com.bracketcove.graphsudoku.ScreenState
import com.bracketcove.graphsudoku.domain.IGameRepository
import com.bracketcove.graphsudoku.ui.newgame.INewGameContainer
import com.bracketcove.graphsudoku.ui.newgame.NewGameEvent
import com.bracketcove.graphsudoku.ui.newgame.NewGameLogic
import com.bracketcove.graphsudoku.ui.newgame.NewGameViewModel

class NewGameContainer(
    val stateHandler: (ScreenState) -> Unit
) : INewGameContainer {

    lateinit var logic: BaseLogic<NewGameEvent>

    fun start(
        vm: NewGameViewModel,
        gameRepo: IGameRepository
    ) {
        logic = NewGameLogic(
            this,
            vm,
            gameRepo
        )

        logic.onEvent(NewGameEvent.OnStart)
    }


    override fun showError(message: String) {
        throw(Exception(message))
    }

    override fun onDoneClick() {
        stateHandler(ScreenState.ACTIVE_GAME)
    }

}