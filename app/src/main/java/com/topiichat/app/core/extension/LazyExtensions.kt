package com.topiichat.app.core.extension

@Suppress("NOTHING_TO_INLINE")
inline fun <T> lazyUnsynchronized(noinline initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)