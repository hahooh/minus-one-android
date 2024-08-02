package com.example.minusone.caller

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

open class Apicaller (private val apiBaseUrl: String) {
    protected suspend fun get(path: String, queries: Map<String,String>): Map<String,Any>? {
        val url = URL(this.apiBaseUrl + path + "?" + this.toQueryString(queries))
        return withContext(Dispatchers.IO) {
            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "GET"
                connection.connect()
                inputStreamToString(connection.inputStream)
            } catch (e: Exception) {
                Log.e("API Caller GET", "Error from api caller: $e")
                null
            } finally {
                connection.disconnect()
            }
        }
    }

    protected suspend fun post(path: String, body: Map<String,Any>): Map<String,Any>? {
        val url = URL(this.apiBaseUrl + path)
        return withContext(Dispatchers.IO) {
            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.connect()

                val writer = OutputStreamWriter(connection.outputStream)
                writer.use {
                    println(mapToJsonString(body))
                    it.write(mapToJsonString(body))
                }

                mapOf(
                    "statusCode" to connection.responseCode,
                    "response" to inputStreamToString(connection.inputStream)
                )
            } catch (e: Exception) {
                Log.e("API Caller POST", "Error from api caller: $e")
                null
            } finally {
                connection.disconnect()
            }
        }
    }

    protected suspend fun delete(path: String, queries: Map<String, String>): Map<String,Any>? {
        val url = URL(this.apiBaseUrl + path + "?" + this.toQueryString(queries))
        return withContext(Dispatchers.IO) {
            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "DELETE"
                connection.connect()
                inputStreamToString(connection.inputStream)
            } catch (e: Exception) {
                Log.e("API Caller DELETE", "Error from api caller: $e")
                null
            } finally {
                connection.disconnect()
            }
        }
    }

    private fun inputStreamToString(inputStream: InputStream): Map<String,Any> {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val response = reader.use { it.readText() }
        val resp = mutableMapOf<String,Any>()
        val jsonObject = JSONObject(response)
        jsonObject.keys().forEach { key ->
            val value = jsonObject[key]
            resp[key] = value
        }
        return resp
    }

    private fun toQueryString(queries: Map<String,String>): String {
        if (queries.isEmpty()) {
            return ""
        }

        var qs = ""
        queries.forEach {
                (key, value) ->
            qs += "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}&"
        }
        return qs.removeSuffix("&")
    }

    private fun mapToJsonString(map: Map<String,Any>): String {
        val gson = Gson()
        return gson.toJson(map)
    }
}