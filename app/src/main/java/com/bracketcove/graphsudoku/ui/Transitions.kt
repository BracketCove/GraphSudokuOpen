package com.bracketcove.graphsudoku.ui

import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.TransitionDefinition
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.bracketcove.graphsudoku.ui.activegame.ActiveGameScreenState

internal val activeAlphaKey = FloatPropKey("active")
internal val completeAlphaKey = FloatPropKey("complete")
internal val loadingAlphaKey = FloatPropKey("loading")

@Composable
internal fun activeGameViewTransition(
    duration: Int = 300,
): TransitionDefinition<ActiveGameScreenState> = remember(duration) {
    transitionDefinition {
        state(ActiveGameScreenState.LOADING) {
            this[activeAlphaKey] = 0f
            this[completeAlphaKey] = 0f
            this[loadingAlphaKey] = 1f
        }

        state(ActiveGameScreenState.ACTIVE) {
            this[activeAlphaKey] = 1f
            this[completeAlphaKey] = 0f
            this[loadingAlphaKey] = 0f
        }

        state(ActiveGameScreenState.COMPLETE) {
            this[activeAlphaKey] = 0f
            this[completeAlphaKey] = 1f
            this[loadingAlphaKey] = 0f
        }

        transition {
            activeAlphaKey using tween(
                durationMillis = duration
            )

            completeAlphaKey using tween(
                durationMillis = duration
            )

            loadingAlphaKey using tween(
                durationMillis = duration
            )
        }
    }
}

internal val newGameAlphaKey = FloatPropKey("active")

@Composable
internal fun newGameViewTransition(
    duration: Int = 500
): TransitionDefinition<Boolean> = remember(duration) {
    transitionDefinition {
        state(true) {
            this[newGameAlphaKey] = 0f
            this[loadingAlphaKey] = 1f
        }

        state(false) {
            this[newGameAlphaKey] = 1f
            this[loadingAlphaKey] = 0f
        }

        transition {
            newGameAlphaKey using tween(
                durationMillis = duration
            )

            loadingAlphaKey using tween(
                durationMillis = duration
            )
        }
    }
}