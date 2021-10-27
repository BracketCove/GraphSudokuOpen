package com.bracketcove.graphsudoku

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

expect object DispatcherProvider {
   fun provideUIContext(): CoroutineContext

   fun provideIOContext(): CoroutineContext
}