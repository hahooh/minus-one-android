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
        holder.iconImageView.setImageDrawable(currentItem.icon)
        val text = "${currentItem.packageName} ${currentItem.usageInSeconds}"
        holder.iconNameTextView.text = text
    }

    class IconViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        val iconNameTextView: TextView = itemView.findViewById(R.id.iconNameTextView)
    }
}