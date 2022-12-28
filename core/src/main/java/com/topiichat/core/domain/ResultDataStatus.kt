package com.topiichat.core.domain

sealed class ResultDataStatus(
    private val message: String
) {
    object Ok : ResultDataStatus("OK")
    object OkWithoutData : ResultDataStatus("OK")
}

sealed class RemoteFailStatus(
    message: String
) : ResultDataStatus(message) {

    data class NoNetwork(
        private val message: String = "no network"
    ) : RemoteFailStatus(message)

    data class ServerUnavailable(
        private val message: String = "server error"
    ) : RemoteFailStatus(message)

    data class NoAuth(
        private val message: String = "no auth"
    ): RemoteFailStatus(message)

    data class Generic(
        private val message: String = "generic error"
    ) : RemoteFailStatus(message)
}

sealed class CacheFailStatus(
    message: String
) : ResultDataStatus(message) {

    data class Write(
        private val message: String = "write error"
    ) : CacheFailStatus(message)

    data class Read(
        private val message: String = "read error"
    ) : CacheFailStatus(message)
}