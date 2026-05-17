package io.github.mamedovilkin.weather.data.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

suspend fun isInternetAvailable(): Boolean = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("https://clients3.google.com/generate_204")
            .build()

        val response = client.newCall(request).execute()
        response.code == 204
    } catch (_: IOException) {
        false
    }
}