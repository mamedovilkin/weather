package io.github.mamedovilkin.weather.network.client

sealed interface Result<T> {
    data class Success<T>(val data: T): Result<T>
    data class Failure<T>(val e: Exception): Result<T>

    fun onSuccess(block: (T) -> Unit): Result<T> {
        if (this is Success) block(data)
        return this
    }

    fun onFailure(block: (Exception) -> Unit): Result<T> {
        if (this is Failure) block(e)
        return this
    }
}

inline fun <T> safeRequest(request: () -> T): Result<T> {
    return try {
        Result.Success(request())
    } catch (e: Exception) {
        Result.Failure(e)
    }
}