package com.bracketcove.graphsudoku.persistence

import com.bracketcove.graphsuduoku.persistence.AppDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import java.io.File

actual class DatabaseDriverFactory()  {
    actual fun createDriver(): SqlDriver {
        val databasePath = File(System.getProperty("java.io.tmpdir"), "SudokuApp.db")
        val driver = JdbcSqliteDriver(url = "jdbc:sqlite:${databasePath.absolutePath}")
        AppDatabase.Schema.create(driver)

        return driver
    }
}