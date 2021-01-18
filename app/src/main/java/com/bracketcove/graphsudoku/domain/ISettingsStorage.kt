package com.bracketcove.graphsudoku.domain

interface ISettingsStorage {
    suspend fun getSettings(): SettingsStorageResult
    suspend fun updateSettings(settings: Settings): SettingsStorageResult
}

sealed class SettingsStorageResult {
    data class OnSuccess(val settings: Settings) : SettingsStorageResult()
    object OnComplete : SettingsStorageResult()
    data class OnError(val exception: Exception) : SettingsStorageResult()
}