package io.github.mamedovilkin.weather.network.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking

class WeatherHttpClientTest : TestCase() {


    fun testGetRequestSuccessReturnsMockedResponse() = runBlocking {
        val mockEngine = MockEngine {
            respond(
                content = "[{\"name\":\"California\"",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine)
        val weatherHttpClient = WeatherHttpClient.getInstance()

        val responseHttpClient = httpClient.get("geo/1.0/direct?q=California&limit=1&appid=2523d05bd7da34810f595b9e12118fd8").body<String>()
        val responseWeatherHttpClient = weatherHttpClient.get("geo/1.0/direct?q=California&limit=1&appid=2523d05bd7da34810f595b9e12118fd8").body<String>()

        assertTrue(responseWeatherHttpClient.startsWith(responseHttpClient))
    }

    fun testGetRequestSuccessReturnsEmptyMockedResponse() = runBlocking {
        val mockEngine = MockEngine {
            respond(
                content = "[]",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine)
        val weatherHttpClient = WeatherHttpClient.getInstance()

        val responseHttpClient = httpClient.get("geo/1.0/direct?q=ejnvntjkbnrtnbjkrnblrnjk&limit=1&appid=2523d05bd7da34810f595b9e12118fd8").body<String>()
        val responseWeatherHttpClient = weatherHttpClient.get("geo/1.0/direct?q=ejnvntjkbnrtnbjkrnblrnjk&limit=1&appid=2523d05bd7da34810f595b9e12118fd8").body<String>()

        assertEquals(responseHttpClient, responseWeatherHttpClient)
    }

    fun testGetRequestFailureReturnsInvalidApiKeyMockedResponse() = runBlocking {
        val mockEngine = MockEngine {
            respond(
                content = "{\"cod\":401, \"message\": \"Invalid API key. Please see https://openweathermap.org/faq#error401 for more info.\"}",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val httpClient = HttpClient(mockEngine)
        val weatherHttpClient = WeatherHttpClient.getInstance()

        val responseHttpClient = httpClient.get("http://api.openweathermap.org/geo/1.0/direct?q=ejnvntjkbnrtnbjkrnblrnjk&limit=1&appid=2523d05bd7da34810f595b9e12118fd").body<String>()
        val responseWeatherHttpClient = weatherHttpClient.get("http://api.openweathermap.org/geo/1.0/direct?q=ejnvntjkbnrtnbjkrnblrnjk&limit=1&appid=2523d05bd7da34810f595b9e12118fd").body<String>()

        assertEquals(responseHttpClient, responseWeatherHttpClient)
    }
}