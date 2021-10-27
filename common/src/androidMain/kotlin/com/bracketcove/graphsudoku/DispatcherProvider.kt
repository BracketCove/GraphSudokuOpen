package com.bracketcove.graphsudoku

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

actual object DispatcherProvider {
    actual fun provideUIContext(): CoroutineContext = Dispatchers.Main

    actual fun provideIOContext(): CoroutineContext = Dispatchers.IO
}