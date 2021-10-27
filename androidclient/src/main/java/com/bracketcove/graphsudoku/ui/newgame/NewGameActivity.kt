package com.bracketcove.graphsudoku.ui.newgame

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.bracketcove.graphsudoku.ui.GraphSudokuTheme
import com.bracketcove.graphsudoku.ui.STR_GENERIC_ERROR
import com.bracketcove.graphsudoku.ui.newgame.buildlogic.buildNewGameLogic

/**
 * This feature is so simple that it is not even worth it to have a separate logic class
 */
class NewGameActivity : AppCompatActivity(), INewGameContainer {
    private lateinit var logic: NewGameLogic


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = NewGameViewModel()

        setContent {
            GraphSudokuTheme {
                NewGameScreen(
                    onEventHandler = logic::onEvent,
                    viewModel
                )
            }
        }

        logic = buildNewGameLogic(viewModel)

    }

    override fun onStart() {
        super.onStart()
        logic.onEvent(NewGameEvent.OnStart)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onDoneClick() {
        startActiveGameActivity()
    }

    /**
     * I want the other feature to be completely restarted each time
     */
    override fun onBackPressed() {
        startActiveGameActivity()
    }

    private fun startActiveGameActivity() {
        startActivity(
            Intent(
                this,
                com.bracketcove.graphsudoku.ui.activegame.ActiveGameActivity::class.java
            ).apply {
                this.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }

}