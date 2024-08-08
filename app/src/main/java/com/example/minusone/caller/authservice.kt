package com.example.minusone.caller

import android.content.Context
import android.util.Log
import androidx.security.crypto.MasterKey
import com.example.minusone.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import androidx.datastore.preferences.preferencesDataStore
import androidx.security.crypto.EncryptedSharedPreferences
import com.google.gson.Gson
import java.net.SocketTimeoutException

data class TokenUser (
    val id: String,
    val email: String,
    val name: String
)

data class TokenResponse (
    val token: String,
    val refreshToken: String,
    val user: TokenUser
)

data class ErrorResponse (
    val message: String,
    val status: UShort,
    val data: Any
)

class AuthService: Apicaller(apiBaseUrl = BuildConfig.API_URL) {
    private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

    fun login(context: Context, email: String, password: String) {
        val body = mutableMapOf<String,String>()
        body["email"] = email
        body["password"] = password
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val loginRequest = async { post(path = "/login", body = body) }
                val (statusCode, data) = loginRequest.await()
                when(statusCode) {
                    HttpURLConnection.HTTP_OK -> handleTokenResponse(context, data)
                }
            } catch (e: Exception) {
                Log.e("Login error", e.toString())
            }
        }
    }

    fun register(context: Context, email: String, password: String, name: String, errorHandler: (String?) -> Unit)  {
        val body = mutableMapOf<String,String>()
        body["email"] = email
        body["password"] = password
        body["name"] = name
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val registerRequest = async { post(path = "/register", body = body ) }
                val (statusCode, data) = registerRequest.await()
                val respMessage = when(statusCode) {
                    in 200..299 -> {
                        handleTokenResponse(context, data)
                        null
                    }
                    in 400..499 -> handleErrorResponse(data)
                    else -> {
                        Log.i("I have data here",data)
                        "Something went wrong"
                    }
                }
                errorHandler(respMessage)
            } catch (e: Exception) {
                Log.e("Register error", e.toString())
                errorHandler("Something went wrong...")
            }
        }
    }

    private fun handleErrorResponse(data: String): String {
        val gson = Gson()
        val errorResp = gson.fromJson(data, ErrorResponse::class.java)
        return errorResp.message
    }

    private fun handleTokenResponse(context: Context, data: String) {
        val gson = Gson()
        val tokenResponse = gson.fromJson(data, TokenResponse::class.java)
        storeToken(context, tokenResponse.token)
    }

    private fun storeToken(context: Context, token: String) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        with(sharedPreferences.edit()) {
            putString("login_token", token)
            apply()
        }
    }

    fun getToken(context: Context) : String? {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        return sharedPreferences.getString("login_token", null)
    }
}

private val authService = AuthService()
fun getAuthService(): AuthService {
    return authService
}