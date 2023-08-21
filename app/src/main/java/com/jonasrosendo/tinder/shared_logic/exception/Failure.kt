package com.jonasrosendo.tinder.shared_logic

data class ErrorExtras(val message: String, val statusCode: Int)

sealed class Failure(val message: String, val extras: ErrorExtras? = null) {
    object NetworkConnection : Failure(NETWORK_CONNECTION_ERROR)
    class ServerError(extras: ErrorExtras? = null) : Failure(SERVER_ERROR, extras)
    object NoDataError : Failure(NO_DATA_FAILURE)
    object UnknownError : Failure(UNKNOWN_ERROR)

    class FeatureFailure(errorMessage: String, extras: ErrorExtras? = null) : Failure(errorMessage, extras)

    companion object {
        const val NETWORK_CONNECTION_ERROR: String = "NETWORK_CONNECTION_ERROR"
        const val SERVER_ERROR: String = "SERVER_ERROR"
        const val UNKNOWN_ERROR: String = "UNKNOWN_ERROR"
        const val NO_DATA_FAILURE: String = "NO_DATA_FAILURE"
    }
}