package com.example.minusone.sampledata

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.minusone.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Calendar

data class UsageApplication (
    val packageName: String,
    val appName: String,
    var usageInSeconds: Long,
    val iconSrc: Int,
    var gCO2e: Double
)

data class ApplicationPackageInfo (
    val appName: String,
    val iconSrc: Int,
    val gCo2ePerMin: Double
)

class UsageStatsService(private val context: Context) {
    private val socialMediaPackageNames = mapOf(
        "com.zhiliaoapp.musically" to ApplicationPackageInfo("TikTok", R.drawable.tiktok,2.63),
        "com.reddit.frontpage" to ApplicationPackageInfo("Reddit",R.drawable.redit,2.48),
        "com.pinterest" to ApplicationPackageInfo("Pinterest",R.drawable.pinterest,1.30),
        "com.instagram.android" to ApplicationPackageInfo("Instagram",R.drawable.instagram,1.05),
        "com.snapchat.android" to ApplicationPackageInfo("Snapchat",R.drawable.snapchat,0.87),
        "com.facebook.katana" to ApplicationPackageInfo("Facebook",R.drawable.facebook,.79),
        "com.linkedin.android" to ApplicationPackageInfo("LinkedIn",R.drawable.linkedin,.71),
        "com.twitter.androidTwitter" to ApplicationPackageInfo("Twitter (X)", R.drawable.twitter,0.60),
        "tv.twitch.android.app" to ApplicationPackageInfo("Twitch",R.drawable.twitch,0.55),
        "com.google.android.youtube" to ApplicationPackageInfo("YouTube",R.drawable.youtube,0.46),
        "com.netflix.mediaclient" to ApplicationPackageInfo("Netflix",R.drawable.netflix,0.93)
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
        calendar.set(Calendar.DAY_OF_MONTH,1)
        calendar.set(Calendar.HOUR_OF_DAY,0)
        calendar.set(Calendar.MINUTE,0)
        calendar.set(Calendar.SECOND,0)
        calendar.set(Calendar.MILLISECOND,0)
        val startTime = calendar.timeInMillis
        calendar.add(Calendar.MONTH,1)
        val endTime = calendar.timeInMillis

        // Query usage stats for the specified time range
        val usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, startTime, endTime)

        // Filter and process the usage data
        val sortedUsageStatsList = usageStatsList.sortedByDescending { it.totalTimeInForeground }

        val timeSpentMap: MutableMap<String, UsageApplication> = mutableMapOf()
        for (usageStats in sortedUsageStatsList) {
            val packageName = usageStats.packageName
            val totalTime = usageStats.totalTimeInForeground / 1000 // Time in seconds
            val usageApplication = timeSpentMap[packageName]
            val packageInfo = socialMediaPackageNames[packageName] ?: continue
            if (usageApplication != null) {
                usageApplication.usageInSeconds += totalTime
                usageApplication.gCO2e = BigDecimal((usageApplication.usageInSeconds / 60) * packageInfo.gCo2ePerMin).setScale(2, RoundingMode.UP).toDouble()
            } else {
                val totalCO2emission = BigDecimal((totalTime / 60) * packageInfo.gCo2ePerMin).setScale(2,RoundingMode.UP).toDouble()
                timeSpentMap[packageName] = UsageApplication(packageName, packageInfo.appName, totalTime, packageInfo.iconSrc, totalCO2emission)
            }
        }

        val usageApplicationList = ArrayList<UsageApplication>()
        for ((_, value) in timeSpentMap) {
            usageApplicationList.add(value)
        }
        return usageApplicationList
    }
}