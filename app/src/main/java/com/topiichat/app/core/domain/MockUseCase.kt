package com.topiichat.app.core.domain

abstract class MockUseCase<in I, O> {

    abstract suspend operator fun invoke(params: I? = null): O

}