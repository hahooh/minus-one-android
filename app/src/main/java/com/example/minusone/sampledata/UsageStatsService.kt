package com.example.minusone.sampledata

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import java.util.Calendar

data class UsageApplication (
    val packageName: String,
    val icon: Drawable,
    val appName: String,
    val usageInSeconds: Long
)

class UsageStatsService(private val context: Context) {
    private val socialMediaPackageNames = mapOf(
        "YouTube" to "com.google.android.youtube",
        "TikTok" to "com.zhiliaoapp.musically",
        "Facebook" to "com.facebook.katana",
        "Reddit" to "com.reddit.frontpage",
        "Instagram" to "com.instagram.android",
        "Twitter (X)" to "com.twitter.android",
        "Snapchat" to "com.snapchat.android",
        "Pinterest" to "com.pinterest",
        "LinkedIn" to "com.linkedin.android",
        "WhatsApp" to "com.whatsapp",
        "Telegram" to "org.telegram.messenger",
        "Discord" to "com.discord",
        "WeChat" to "com.tencent.mm",
        "Clubhouse" to "com.clubhouse.app",
        "Tumblr" to "com.tumblr",
        "Viber" to "com.viber.voip",
        "Messenger (Facebook Messenger)" to "com.facebook.orca"
    )


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
            val socialMediaName = socialMediaPackageNames[packageName] ?: continue
            usageApplicationList.add(UsageApplication(packageName, icon, socialMediaName, totalTime/1000))
        }
        return usageApplicationList
    }
}