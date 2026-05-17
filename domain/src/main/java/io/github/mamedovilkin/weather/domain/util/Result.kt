package io.github.mamedovilkin.weather.domain.util

sealed interface Result<T> {
    data class Success<T>(val data: T, val isOffline: Boolean): Result<T>
    data class Failure<T>(val e: Exception): Result<T>

    fun onSuccess(block: (T, Boolean) -> Unit): Result<T> {
        if (this is Success) block(data, isOffline)
        return this
    }

    fun onFailure(block: (Exception) -> Unit): Result<T> {
        if (this is Failure) block(e)
        return this
    }
}

inline fun <T> safeRequest(request: () -> T, isOffline: Boolean): Result<T> {
    return try {
        Result.Success(request(), isOffline)
    } catch (e: Exception) {
        Result.Failure(e)
    }
}