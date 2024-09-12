package com.example.minusone.caller
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

data class Error (
    val message: String,
    val status: Int,
    val data: Map<String,String>?,
)

data class Response<T>(
    val statusCode: Int,
    val data: T?,
    val errorResponse: Error?,
)

open class Apicaller(protected val apiBaseUrl: String) {
    protected val gson = Gson()

    protected suspend inline fun <reified T> get(path: String, queries: Map<String, String>): Response<T> {
        val url = URL(this.apiBaseUrl + path + "?" + this.toQueryString(queries))
        return withContext(Dispatchers.IO) {
            val connection = url.openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "GET"
                connection.connect()

                val responseCode = connection.responseCode
                val responseString = if (responseCode in 200..299) {
                    inputStreamToString(connection.inputStream)
                } else {
                    inputStreamToString(connection.errorStream)
                }

                if (responseCode in 200..299) {
                    val result = gson.fromJson<T>(responseString, object : TypeToken<T>() {}.type)
                    Response(responseCode,result,null)
                } else {
                    val result = gson.fromJson<Error>(responseString, Error::class.java)
                    Response(responseCode, null, result)
                }
            } catch (e: Exception) {
                Log.e("API Caller GET", "Error from api caller: $e")
                Response(HttpURLConnection.HTTP_INTERNAL_ERROR, null,null)
            } finally {
                connection.disconnect()
            }
        }
    }

    protected suspend inline fun <reified T> post(path: String, body: T): Response<T> {
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
                    it.write(gson.toJson(body))
                }

                val responseCode = connection.responseCode
                val responseStream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
                val responseString = inputStreamToString(responseStream)

                if (responseCode in 200..299) {
                    val result = gson.fromJson<T>(responseString, object : TypeToken<T>() {}.type)
                    Response(responseCode,result,null)
                } else {
                    val err = gson.fromJson<Error>(responseString,Error::class.java)
                    Response(responseCode,null,err)
                }
            } catch (e: Exception) {
                Log.e("API Caller POST", "Error from api caller: $e")
                Response(HttpURLConnection.HTTP_INTERNAL_ERROR,null,null)
            } finally {
                connection.disconnect()
            }
        }
    }

    protected fun inputStreamToString(inputStream: InputStream): String {
        val reader = BufferedReader(InputStreamReader(inputStream))
        return reader.use { it.readText() }
    }

    protected fun toQueryString(queries: Map<String, String>): String {
        if (queries.isEmpty()) return ""
        return queries.entries.joinToString("&") { (key, value) ->
            "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
        }
    }
}
