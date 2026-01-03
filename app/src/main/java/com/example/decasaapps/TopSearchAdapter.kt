package com.example.decasaapps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.decasaapps.model.Property

class TopSearchAdapter(private val list: List<Property>) :
    RecyclerView.Adapter<TopSearchAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivThumb: ImageView = view.findViewById(R.id.ivThumb)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        val tvCity: TextView = view.findViewById(R.id.tvCity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvTitle.text = item.title
        
        // Split location for demo: "Ago, Lagos" -> Address="Ago", City="Lagos"
        val locationParts = item.location.split(",")
        if (locationParts.size > 1) {
             holder.tvAddress.text = locationParts[0] + ", "
             holder.tvCity.text = locationParts[1]
        } else {
             holder.tvAddress.text = item.location
             holder.tvCity.text = ""
        }

        Glide.with(holder.itemView.context)
            .load(item.imageResId)
            .into(holder.ivThumb)
    }

    override fun getItemCount(): Int = list.size
}
