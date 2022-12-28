package com.topiichat.app.features.registration.data.datasource.cache

import android.content.SharedPreferences
import com.topiichat.app.features.registration.data.model.AuthDataDto
import com.topiichat.app.features.registration.domain.model.AuthDataDomain
import com.topiichat.core.data.EmptyDto

class RegisterCacheImpl(
    private val pref: SharedPreferences
) : RegisterCache {

    override suspend fun fetchAccessToken(): AuthDataDto = with(pref) {
        val token = getString(ACCESS_TOKEN_KEY, null)
        val senderId = getString(SENDER_ID_KEY, null)
        val isoCode = getString(ISO_CODE_KEY, null)
        return@with AuthDataDto(token = token, senderId = senderId, isoCode = isoCode)
    }

    override suspend fun saveAccessToken(tokenDomain: AuthDataDomain) = with(pref.edit()) {
        putString(ACCESS_TOKEN_KEY, tokenDomain.token)
        putString(SENDER_ID_KEY, tokenDomain.senderId)
        putString(ISO_CODE_KEY, tokenDomain.isoCode)
        apply()
        return@with EmptyDto
    }

    override suspend fun deleteAuthData() = with(pref.edit()) {
        putString(ACCESS_TOKEN_KEY, null)
        putString(SENDER_ID_KEY, null)
        putString(ISO_CODE_KEY, null)
        apply()
        return@with EmptyDto
    }

    companion object {
        const val ACCESS_TOKEN_KEY = "access_token_key"
        const val SENDER_ID_KEY = "sender_id_key"
        const val ISO_CODE_KEY = "iso_code_id_key"
    }
}