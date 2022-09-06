package com.topiichat.app.core.domain

interface BaseMapper<I, O> {
    fun map(input: I): O
}
