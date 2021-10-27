package com.bracketcove.graphsudoku.persistence

import com.bracketcove.graphsudoku.domain.DIFFICULTY
import com.bracketcove.graphsudoku.domain.Settings
import com.bracketcove.graphsudoku.domain.UserRecords
import com.bracketcove.graphsuduoku.persistence.AppDatabase
import com.squareup.sqldelight.EnumColumnAdapter


private const val DEFAULT_PRIMARY_KEY = 1L

/**
 * What's going on here?
 *
 * I didn't have enough time to review options for multiplatform storage of simple data, so I
 * elected to store in an SQLDelight database. This isn't necessary the best use case for storing
 * what is essentially a single column; but I like the library and don't mind demoing it.
 */
class UserDataStorage(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDatabase(
        databaseDriverFactory.createDriver(),
        UserData.Adapter(
            EnumColumnAdapter()
        )
    )

    private val dbQuery = database.appDatabaseQueries

    internal fun createDefaults() {
        dbQuery.insertDefault(
            DEFAULT_PRIMARY_KEY,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            DEFAULT_BOUNDARY,
            DEFAULT_DIFFICULTY
        )
    }

    internal fun updateRecord(
        record: Long,
        boundary: Int,
        difficulty: DIFFICULTY
    ): UserDataStorageResult {
        try {
            val oldData = dbQuery.select(DEFAULT_PRIMARY_KEY).executeAsOne()

            val oldTime = oldData.findMatch(difficulty, boundary)

            if (oldTime > record || oldTime == 0L) {
                val newData = oldData.updateMatch(record, difficulty, boundary)

                dbQuery.updateRecord(
                    DEFAULT_PRIMARY_KEY,
                    newData.fourEasy,
                    newData.fourMedium,
                    newData.fourHard,
                    newData.nineEasy,
                    newData.nineMedium,
                    newData.nineHard
                )

                return UserDataStorageResult.SUCCESS_RECORDS(
                    UserRecords(
                        newData.fourEasy,
                        newData.fourMedium,
                        newData.fourHard,
                        newData.nineEasy,
                        newData.nineMedium,
                        newData.nineHard
                    ), true
                )
            } else {
                return UserDataStorageResult.SUCCESS_RECORDS(
                    UserRecords(
                        oldData.fourEasy,
                        oldData.fourMedium,
                        oldData.fourHard,
                        oldData.nineEasy,
                        oldData.nineMedium,
                        oldData.nineHard
                    ),
                    false
                )
            }
        } catch (e: Exception) {
            return UserDataStorageResult.ERROR(Exception(e.message))
        }
    }

    internal fun getRecords(): UserDataStorageResult {
        return try {
            UserDataStorageResult.SUCCESS_RECORDS(
                dbQuery.selectRecord(DEFAULT_PRIMARY_KEY).executeAsOne().toRecords(),
                false
            )
        } catch (e: Exception) {
            UserDataStorageResult.ERROR(Exception(e.message))
        }
    }

    internal fun updateSettings(
        settings: Settings
    ): UserDataStorageResult {
        return try {
            dbQuery.updateGameSettings(
                settings.boundary,
                settings.difficulty,
                DEFAULT_PRIMARY_KEY
            )

            UserDataStorageResult.SUCCESS_SETTINGS(settings)
        } catch (e: Exception) {
            UserDataStorageResult.ERROR(Exception(e.message))
        }
    }

    internal fun getSettings(): UserDataStorageResult {
        return try {
            UserDataStorageResult.SUCCESS_SETTINGS(
                dbQuery.selectGameSettings(DEFAULT_PRIMARY_KEY).executeAsOne().toSettings()
            )
        } catch (e: Exception) {
            UserDataStorageResult.ERROR(Exception(e.message))
        }
    }

    private fun UserData.findMatch(diff: DIFFICULTY, boundary: Int): Long {
        return when {
            diff == DIFFICULTY.EASY && boundary == 4 -> fourEasy
            diff == DIFFICULTY.MEDIUM && boundary == 4 -> fourMedium
            diff == DIFFICULTY.HARD && boundary == 4 -> fourHard

            diff == DIFFICULTY.EASY && boundary == 9 -> nineEasy
            diff == DIFFICULTY.MEDIUM && boundary == 9 -> nineMedium
            diff == DIFFICULTY.HARD && boundary == 9 -> nineHard

            else -> throw Exception("Unable to match incoming data with stored data")
        }
    }

    private fun UserData.updateMatch(
        time: Long,
        diff: DIFFICULTY,
        boundary: Int
    ): UserData {
        return when {
            diff == DIFFICULTY.EASY && boundary == 4 -> this.copy(fourEasy = time)
            diff == DIFFICULTY.MEDIUM && boundary == 4 -> this.copy(fourMedium = time)
            diff == DIFFICULTY.HARD && boundary == 4 -> this.copy(fourHard = time)

            diff == DIFFICULTY.EASY && boundary == 9 -> this.copy(nineEasy = time)
            diff == DIFFICULTY.MEDIUM && boundary == 9 -> this.copy(nineMedium = time)
            diff == DIFFICULTY.HARD && boundary == 9 -> this.copy(nineHard = time)

            else -> throw Exception("Unable to match and update stored user data")
        }
    }

    private fun SelectGameSettings.toSettings(): Settings = Settings(
        difficulty,
        boundary
    )

    private fun SelectRecord.toRecords(): UserRecords = UserRecords(
        fourEasy,
        fourMedium,
        fourHard,
        nineEasy,
        nineMedium,
        nineHard
    )
}

sealed class UserDataStorageResult {
    data class SUCCESS_SETTINGS(val settings: Settings) : UserDataStorageResult()
    data class SUCCESS_RECORDS(val records: UserRecords, val isRecord: Boolean) :
        UserDataStorageResult()

    data class ERROR(val error: Exception) : UserDataStorageResult()
}