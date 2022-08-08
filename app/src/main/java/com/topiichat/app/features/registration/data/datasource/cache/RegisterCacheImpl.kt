package com.topiichat.app.features.registration.data.datasource.cache

import android.content.SharedPreferences
import com.topiichat.app.core.data.EmptyDto
import com.topiichat.app.features.registration.data.model.AccessTokenDto
import com.topiichat.app.features.registration.domain.model.AccessTokenDomain

class RegisterCacheImpl(
    private val pref: SharedPreferences
) : RegisterCache {

    override suspend fun fetchAccessToken(): AccessTokenDto = with(pref) {
        val token = getString(ACCESS_TOKEN_KEY, null)
        return@with AccessTokenDto(token = token)
    }

    override suspend fun saveAccessToken(tokenDomain: AccessTokenDomain) = with(pref.edit()) {
        putString(ACCESS_TOKEN_KEY, tokenDomain.token)
        apply()
        return@with EmptyDto
    }

    companion object {
        const val ACCESS_TOKEN_KEY = "access_token_key"
    }
}