package com.topiichat.core.coroutines

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Using for switch among [CoroutineDispatcher]'s instead of using
 * directly [kotlinx.coroutines.Dispatchers]
 */
interface AppDispatchers {
    /**
     * For UI tasks
     */
    val ui: CoroutineDispatcher

    /**
     * For tasks like write to file or access to DB
     * Can be used to access from resources\raw or assets,
     * or for sync write into [android.content.SharedPreferences]
     */
    val storage: CoroutineDispatcher

    /**
     * For network-based operations
     */
    val network: CoroutineDispatcher

    /**
     * For massive algorithmic operations, so we can not use [main], [storage] or [network].
     * Sort, parsing should be done in [computing]
     */
    val computing: CoroutineDispatcher
}