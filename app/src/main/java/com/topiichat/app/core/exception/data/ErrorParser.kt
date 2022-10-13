package com.topiichat.app.core.exception.data

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.topiichat.app.core.exception.data.model.ServiceUnavailableException
import com.topiichat.app.core.exception.domain.ErrorDomain
import com.topiichat.app.core.exception.mapper.ErrorMessageMapper
import retrofit2.HttpException
import timber.log.Timber
import java.net.UnknownHostException

class ErrorParser {

    val defaultError =
        ErrorDomain("Service unavailable", 500, ServiceUnavailableException::class.java)

    val unAuthorizedError =
        ErrorDomain("Unauthorized", 401, UnknownHostException::class.java)

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val errorMessageMapper = ErrorMessageMapper()

    fun parse(e: Throwable?): ErrorDomain {
        return try {
            val httpError = (e as HttpException)
            val parsedBody = httpError.response()?.errorBody()?.string().toString()
            val code = httpError.code()
            Timber.d(e.message())
            if (code == 401) return unAuthorizedError
            try {
                val adapter = moshi.adapter(ErrorMessageDto::class.java)
                val errorMessageDto = adapter.fromJson(parsedBody)
                ErrorDomain(errorMessageMapper.map(errorMessageDto), code, e.javaClass)
            } catch (e: JsonEncodingException) {
                Timber.e(e)
                defaultError
            } catch (e: JsonDataException) {
                Timber.e(e)
                defaultError
            } catch (e: NullPointerException) {
                Timber.e(e)
                defaultError
            }
        } catch (_: ClassCastException) {
            defaultError
        }
    }
}