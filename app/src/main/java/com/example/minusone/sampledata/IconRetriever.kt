package com.example.minusone.sampledata

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable


class IconRetriever(private val packageManager: PackageManager) {
    fun getAppIcon(packageName: String): Drawable? {
        try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            return packageManager.getApplicationIcon(appInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }
    }
}