package com.example.minusone

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.minusone.sampledata.UsageStatsService

class DashboardActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        val usageStatsService = UsageStatsService(this)
        if(!usageStatsService.isUsageStatsPermissionGranted()) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
        }

        if(!usageStatsService.isUsageStatsPermissionGranted()) {
            Log.i("permission checker", "I don't have your permission")
            return
        }

        val usageApplications = usageStatsService.getUsageStats()
        for (usageApplication in usageApplications) {
            Log.i("Usage Application", usageApplication.packageName)
            Log.i("Usage Application", usageApplication.usageInSeconds.toString())
        }
    }
}