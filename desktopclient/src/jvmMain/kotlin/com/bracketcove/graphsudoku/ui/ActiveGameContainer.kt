package com.bracketcove.graphsudoku.ui

import com.bracketcove.graphsudoku.ScreenState
import com.bracketcove.graphsudoku.domain.IGameRepository
import com.bracketcove.graphsudoku.ui.activegame.ActiveGameEvent
import com.bracketcove.graphsudoku.ui.activegame.ActiveGameLogic
import com.bracketcove.graphsudoku.ui.activegame.ActiveGameViewModel
import com.bracketcove.graphsudoku.ui.activegame.IActiveGameContainer

class ActiveGameContainer(
    val stateHandler: (ScreenState) -> Unit
) : IActiveGameContainer  {

    lateinit var logic: BaseLogic<ActiveGameEvent>

    fun start(
        vm: ActiveGameViewModel,
        gameRepo: IGameRepository
    ) {
        logic = ActiveGameLogic(
            this,
            vm,
            gameRepo
        )

        logic.onEvent(ActiveGameEvent.OnStart)
    }

    override fun showError(message: String) {
        throw(Exception(message))
    }

    override fun onNewGameClick() {
        stateHandler(ScreenState.NEW_GAME)
    }

}