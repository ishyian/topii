package com.topiichat.app.data.core

import com.topiichat.core.coroutines.AppDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@ExperimentalCoroutinesApi
class TestAppDispatchers : AppDispatchers {
    override val ui = UnconfinedTestDispatcher(TestCoroutineScheduler())
    override val storage = UnconfinedTestDispatcher(TestCoroutineScheduler())
    override val network = UnconfinedTestDispatcher(TestCoroutineScheduler())
    override val computing = UnconfinedTestDispatcher(TestCoroutineScheduler())
}