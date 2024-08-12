package com.example.minusone

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class DashboardActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        val permissionChecker = PermissionChecker(this)
        if(!permissionChecker.isUsageStatsPermissionGranted()) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
        }

        if(!permissionChecker.isUsageStatsPermissionGranted()) {
            Log.i("permission checker", "I don't have your permission")
            return
        }

        getUsageStats()
    }

    fun getUsageStats() {
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, -1) // Set the range for the past day
        val startTime = calendar.timeInMillis

        // Query usage stats for the specified time range
        val usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)

        // Filter and process the usage data
        val sortedUsageStatsList = usageStatsList.sortedByDescending { it.totalTimeInForeground }

        // Log or process the usage stats as needed
        for (usageStats in sortedUsageStatsList) {
            val packageName = usageStats.packageName
            val totalTime = usageStats.totalTimeInForeground // Time in milliseconds
            Log.d("AppUsageHelper", "Package: $packageName, Time in foreground: ${totalTime / 1000} seconds")
        }
    }
}