package com.lethargicrunner.mailclean.http

import java.net.HttpURLConnection
import java.net.URL

/**
 * Unique classe HTTP de l'app (même convention que CloudClean) : tout le réseau passe ici,
 * y compris les appels Gmail API / Microsoft Graph. Le moteur IA local n'en a pas besoin.
 */
object HttpJsonClient {

    class HttpResult(val code: Int, val body: String)

    fun get(url: String, accessToken: String? = null): HttpResult = request("GET", url, null, accessToken)

    fun post(url: String, body: String, accessToken: String? = null, contentType: String = "application/json"): HttpResult =
        request("POST", url, body, accessToken, contentType)

    private fun request(method: String, url: String, body: String?, accessToken: String?, contentType: String = "application/json"): HttpResult {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 15000
            readTimeout = 20000
            if (accessToken != null) setRequestProperty("Authorization", "Bearer $accessToken")
            if (body != null) {
                doOutput = true
                setRequestProperty("Content-Type", contentType)
                setFixedLengthStreamingMode(body.toByteArray(Charsets.UTF_8).size)
            }
        }
        try {
            if (body != null) conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() } ?: ""
            return HttpResult(code, text)
        } finally {
            conn.disconnect()
        }
    }
}
