package com.example.minusone.caller

import android.content.Context
import com.example.minusone.BuildConfig

data class  TargetApplication (
    val id: String,
    val name: String,
    val iconUrl: String,
    val gCO2e: Double,
)

class targetapplicationservice: Apicaller(apiBaseUrl = BuildConfig.API_URL) {
    fun loadAll(context: Context, callBack: ())
}