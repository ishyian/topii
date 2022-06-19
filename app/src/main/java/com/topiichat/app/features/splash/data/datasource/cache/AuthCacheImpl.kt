package com.topiichat.app.features.splash.data.datasource.cache

import android.content.SharedPreferences
import com.topiichat.app.core.data.EmptyDto
import com.topiichat.app.features.splash.data.model.TokenCacheDto
import com.topiichat.app.features.splash.domain.model.TokenDomain
import javax.inject.Inject

class AuthCacheImpl @Inject constructor(
    private val pref: SharedPreferences
) : AuthCache {

    override suspend fun fetchToken(): TokenCacheDto = with(pref) {
        val token = getString(PREF_TOKEN, "") ?: ""
        val expireOfSeconds = getInt(PREF_EXPIRE, 0)
        return TokenCacheDto(
            token = token,
            expireOfSeconds = expireOfSeconds
        )
    }

    override suspend fun saveToken(
        token: TokenDomain
    ): EmptyDto = with(pref.edit()) {
        putString(PREF_TOKEN, token.token)
        putInt(PREF_EXPIRE, token.expireOfSeconds)
        apply()
        return EmptyDto
    }

    companion object {
        private const val PREF_TOKEN = "token"
        private const val PREF_EXPIRE = "expire"
    }
}