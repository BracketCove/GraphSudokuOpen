package com.bracketcove.graphsudoku.common

import android.app.Activity
import android.widget.Toast
import com.bracketcove.graphsudoku.R
import com.bracketcove.graphsudoku.domain.Difficulty

internal fun Activity.makeToast(message: String) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_LONG
    ).show()
}

inteernal fun Long.toTime(): String {
    if (this >= 3600) return "+59:59"
    var minutes = (this % 3600) / 60
    var seconds = this % 60
    return "%02d:%02d".format(minutes, seconds)
}

internal val Difficulty.toLocalizedResource: Int
    get() {
        return when (this) {
            Difficulty.EASY -> R.string.easy
            Difficulty.MEDIUM -> R.string.medium
            Difficulty.HARD -> R.string.hard
        }
    }
