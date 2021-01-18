package com.bracketcove.graphsudoku.common

import kotlinx.coroutines.Job

abstract class BaseLogic<EVENT>(val dispatcher: DispatcherProvider)  {
    protected lateinit var jobTracker: Job
    abstract fun onEvent(event: EVENT)
}