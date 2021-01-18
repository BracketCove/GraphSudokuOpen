package com.bracketcove.graphsudoku.ui.activegame

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import com.bracketcove.graphsudoku.common.getStringRes
import com.bracketcove.graphsudoku.common.makeToast
import com.bracketcove.graphsudoku.domain.Messages
import com.bracketcove.graphsudoku.ui.newgame.NewGameActivity
import java.util.*

class ActiveGameActivity : AppCompatActivity(), ActiveGameContainer {
    private lateinit var logic: ActiveGameLogic

    internal object BuildTimer {
        val timer = Timer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ActiveGameViewModel()

        setContent {
            ActiveGameScreen(
                onEventHandler = {
                    logic.onEvent(it)
                },
                viewModel
            )
        }

        logic = buildActiveGameLogic(this, viewModel, applicationContext)
    }



    override fun onStart() {
        super.onStart()
        logic.onEvent(ActiveGameEvent.OnStart)
    }

    override fun onStop() {
        super.onStop()
        logic.onEvent(ActiveGameEvent.OnStop)

        //guarantee that onRestart not called

        finish()
    }

    override fun onNewGameClick() {
        startActivity(
            Intent(
                this,
                NewGameActivity::class.java
            )
        )
    }

    override fun showMessage(message: Messages) = makeToast(getStringRes(message))

    override fun restart() {
        startActivity(
            Intent(
                this,
                ActiveGameActivity::class.java
            )
        )

        finish()
    }
}
