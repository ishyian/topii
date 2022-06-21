package com.topiichat.app.core.data

import com.topiichat.app.core.domain.EmptyDomain
import com.topiichat.app.core.domain.Mapper
import javax.inject.Inject

class EmptyMapper @Inject constructor() : Mapper<EmptyDto, EmptyDomain> {

    override fun map(input: EmptyDto?): EmptyDomain {
        return EmptyDomain
    }
}