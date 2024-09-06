package com.example.minusone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.minusone.sampledata.UsageApplication

class IconAdapter (private val iconList: List<UsageApplication>) : RecyclerView.Adapter<IconAdapter.IconViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.applicatoin_icon, parent, false)
        return IconViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.iconList.count()
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val currentItem = iconList[position]
        holder.iconImageView.setImageResource(currentItem.iconSrc)
        holder.iconNameTextView.text = currentItem.appName
        holder.appTimeSpentView.text = this.formatSeconds(currentItem.usageInSeconds)
        holder.co2eView.text = "${currentItem.gCO2e} gCO2e"
    }

    private fun formatSeconds(seconds: Long): String {
        val hourInSeconds = 60 * 60
        val minInSeconds = 60
        val hours = seconds / hourInSeconds
        val minutes = (seconds % hourInSeconds) / minInSeconds
        val secs = seconds % minInSeconds

        // Format each component to always have at least 2 digits, with leading zeroes if needed
        val formattedHours = String.format("%03d", hours) // Ensures 3 digits for hours
        val formattedMinutes = String.format("%02d", minutes) // Ensures 2 digits for minutes
        val formattedSeconds = String.format("%02d", secs) // Ensures 2 digits for seconds
        return "${formattedHours}h ${formattedMinutes}m ${formattedSeconds}s"
    }

    class IconViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        val iconNameTextView: TextView = itemView.findViewById(R.id.appNameText)
        val appTimeSpentView: TextView = itemView.findViewById(R.id.appTimeSpend)
        val co2eView: TextView = itemView.findViewById(R.id.gCO2e)
    }
}