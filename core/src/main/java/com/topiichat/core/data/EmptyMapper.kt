package com.topiichat.core.data

import com.topiichat.core.domain.EmptyDomain
import com.topiichat.core.domain.Mapper
import javax.inject.Inject

class EmptyMapper @Inject constructor() : Mapper<EmptyDto, EmptyDomain> {

    override fun map(input: EmptyDto?): EmptyDomain {
        return EmptyDomain
    }
}