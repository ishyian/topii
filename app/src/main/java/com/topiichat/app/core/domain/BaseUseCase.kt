package com.topiichat.app.core.domain

abstract class BaseUseCase<in I, O> {

    abstract suspend operator fun invoke(params: I? = null): O

}