package com.bracketcove.graphsudoku


import com.bracketcove.graphsudoku.domain.DIFFICULTY
import com.bracketcove.graphsudoku.ui.STR_DIFF_EASY
import com.bracketcove.graphsudoku.ui.STR_DIFF_HARD
import com.bracketcove.graphsudoku.ui.STR_DIFF_MED


internal fun Long.toTime(): String {
    if (this >= 3600) return "+59:59"
    var minutes = ((this % 3600) / 60).toString()
    if (minutes.length == 1) minutes = "0$minutes"
    var seconds = (this % 60).toString()
    if (seconds.length == 1) seconds = "0$seconds"
    return "$minutes:$seconds"
}

internal val DIFFICULTY.toLocalizedResource: String
    get() {
        return when (this) {
            DIFFICULTY.EASY -> STR_DIFF_EASY
            DIFFICULTY.MEDIUM -> STR_DIFF_MED
            DIFFICULTY.HARD -> STR_DIFF_HARD
        }
    }
