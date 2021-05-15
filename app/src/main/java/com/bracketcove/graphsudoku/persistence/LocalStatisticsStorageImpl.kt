package com.bracketcove.graphsudoku.persistence

import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import com.bracketcove.graphsudoku.GameSettings
import com.bracketcove.graphsudoku.Statistics
import com.bracketcove.graphsudoku.domain.Difficulty
import com.bracketcove.graphsudoku.domain.IStatisticsRepository
import com.bracketcove.graphsudoku.domain.SettingsStorageResult
import com.bracketcove.graphsudoku.domain.UserStatistics
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class LocalStatisticsStorageImpl(
    private val dataStore: DataStore<Statistics>
) : IStatisticsRepository {
    override suspend fun getStatistics(
        onSuccess: (UserStatistics) -> Unit,
        onError: (Exception) -> Unit
    ) = withContext(Dispatchers.IO) {
        try {
            val stats = dataStore.data.first()

            onSuccess(
                stats.toUserStatistics
            )
        } catch (e: Exception) {
            onError(e)
        }
    }

    override suspend fun updateStatistic(
        time: Long,
        diff: Difficulty,
        boundary: Int,
        onSuccess: (isRecord: Boolean) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val stats = dataStore.data.first()

            val oldTime = stats.findMatch(diff, boundary)

            if (oldTime > time || oldTime == 0L) {
                val userStats = stats.toUserStatistics.updateMatch(time, diff, boundary)
                dataStore.updateData { stats ->
                    stats.toBuilder()
                        .setFourEasy(userStats.fourEasy)
                        .setFourMedium(userStats.fourMedium)
                        .setFourHard(userStats.fourHard)
                        .setNineEasy(userStats.nineEasy)
                        .setNineMedium(userStats.nineMedium)
                        .setNineHard(userStats.nineHard)
                        .build()
                }

                onSuccess(true)
            } else {
                onSuccess(false)
            }

        } catch (e: Exception) {
            onError(e)
        }
    }

    private val Statistics.toUserStatistics: UserStatistics
        get() {
           return UserStatistics(
                this.fourEasy,
                this.fourMedium,
                this.fourHard,
                this.nineEasy,
                this.nineMedium,
                this.nineHard
            )
        }

    private fun Statistics.findMatch(diff: Difficulty, boundary: Int): Long {
        return when {
            diff == Difficulty.EASY && boundary == 4 -> fourEasy
            diff == Difficulty.MEDIUM && boundary == 4 -> fourMedium
            diff == Difficulty.HARD && boundary == 4 -> fourHard

            diff == Difficulty.EASY && boundary == 9 -> nineEasy
            diff == Difficulty.MEDIUM && boundary == 9 -> nineMedium
            diff == Difficulty.HARD && boundary == 9 -> nineHard

            else -> throw IOException()
        }
    }

    private fun UserStatistics.updateMatch(
        time: Long,
        diff: Difficulty,
        boundary: Int
    ): UserStatistics {
       return when {
            diff == Difficulty.EASY && boundary == 4 -> this.copy(fourEasy = time)
            diff == Difficulty.MEDIUM && boundary == 4 -> this.copy(fourMedium = time)
            diff == Difficulty.HARD && boundary == 4 -> this.copy(fourHard = time)

            diff == Difficulty.EASY && boundary == 9 -> this.copy(nineEasy = time)
            diff == Difficulty.MEDIUM && boundary == 9 -> this.copy(nineMedium = time)
            diff == Difficulty.HARD && boundary == 9 -> this.copy(nineHard = time)

            else -> throw IOException()
        }
    }
}