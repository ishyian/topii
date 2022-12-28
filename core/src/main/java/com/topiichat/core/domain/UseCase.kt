package com.topiichat.core.domain

interface UseCase<in I, O> {
    suspend operator fun invoke(params: I? = null): ResultData<O>
}