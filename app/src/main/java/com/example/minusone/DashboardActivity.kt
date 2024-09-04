package com.example.minusone

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val usageApplications = usageStatsService.getUsageStats()
        val adapter = IconAdapter(usageApplications)
        recyclerView.adapter = adapter
    }
}