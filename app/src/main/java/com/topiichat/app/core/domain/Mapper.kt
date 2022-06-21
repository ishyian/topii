package com.topiichat.app.core.domain

interface Mapper<I, O> {
    fun map(input: I?): O
}
