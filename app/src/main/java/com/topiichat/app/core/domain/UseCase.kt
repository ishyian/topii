package com.topiichat.app.core.domain

abstract class UseCase<in I, out O> {

    abstract operator fun invoke(params: I? = null): O

}