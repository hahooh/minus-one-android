package com.example.minusone.sampledata

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import java.util.Calendar

data class UsageApplication (
    val packageName: String,
    val icon: Drawable,
    val usageInSeconds: Long
)

class UsageStatsService(private val context: Context) {

    // Check if the usage stats permission is granted
    fun isUsageStatsPermissionGranted(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun getUsageStats(): ArrayList<UsageApplication> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val startTime = calendar.timeInMillis

        // Query usage stats for the specified time range
        val usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)

        // Filter and process the usage data
        val sortedUsageStatsList = usageStatsList.sortedByDescending { it.totalTimeInForeground }

        val iconRetriever = IconRetriever(context.packageManager)
        val usageApplicationList = ArrayList<UsageApplication>()
        // Log or process the usage stats as needed
        for (usageStats in sortedUsageStatsList) {
            val packageName = usageStats.packageName
            val totalTime = usageStats.totalTimeInForeground // Time in milliseconds
            val icon = iconRetriever.getAppIcon(packageName) ?: continue
            usageApplicationList.add(UsageApplication(packageName, icon, totalTime/1000))
        }
        return usageApplicationList
    }
}