package com.bracketcove.graphsudoku.persistence

import android.content.Context
import com.bracketcove.graphsuduoku.persistence.AppDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema, context, "dayplanner.db")
    }
}