package com.bracketcove.graphsudoku.ui.activegame

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.bracketcove.graphsudoku.ui.GraphSudokuTheme
import com.bracketcove.graphsudoku.ui.STR_GENERIC_ERROR
import com.bracketcove.graphsudoku.ui.activegame.buildlogic.buildActiveGameLogic
import com.bracketcove.graphsudoku.ui.newgame.NewGameActivity

class ActiveGameActivity : AppCompatActivity(), IActiveGameContainer {
    private lateinit var logic: ActiveGameLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ActiveGameViewModel()

        setContent {
            GraphSudokuTheme {
                ActiveGameScreen(
                    onEventHandler = logic::onEvent,
                    viewModel
                )
            }
        }

        logic = buildActiveGameLogic(viewModel)
    }

    override fun onStart() {
        super.onStart()
        logic.onEvent(ActiveGameEvent.OnStart)
    }

    override fun onStop() {
        super.onStop()
        logic.onEvent(ActiveGameEvent.OnStop)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onNewGameClick() {
        startActivity(
            Intent(
                this,
                NewGameActivity::class.java
            )
        )
    }
}