package com.example.decasaapps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
// import com.example.decasaapps.model.PropertyData // REMOVED: Broken import

class TopSearchAdapter(private val list: List<PropertyData>) :
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

        // 1. PERBAIKAN: Gunakan 'name' (PropertyData)
        holder.tvTitle.text = item.name

        // 2. PERBAIKAN: Gunakan 'location' (PropertyData)
        val rawLocation = item.location
        val locationParts = rawLocation.split(",")

        if (locationParts.size > 1) {
            holder.tvAddress.text = locationParts[0].trim() + ","
            holder.tvCity.text = locationParts[1].trim()
        } else {
            holder.tvAddress.text = rawLocation
            holder.tvCity.text = ""
        }

        // 3. PERBAIKAN: Gunakan 'imageUrl' (PropertyData) dan Glide
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.kamar_mewah) // Sesuaikan dengan placeholder yang ada
            .error(R.drawable.ic_launcher_background) 
            .into(holder.ivThumb)

        // Add Click Listener
        holder.itemView.setOnClickListener {
            val intent = android.content.Intent(holder.itemView.context, com.example.decasaapps.DetailActivity::class.java)
            intent.putExtra("EXTRA_NAME", item.name)
            intent.putExtra("EXTRA_LOCATION", item.location)
            intent.putExtra("EXTRA_IMAGE", item.imageUrl)
            intent.putExtra("EXTRA_PRICE", item.price)
            intent.putExtra("EXTRA_RATING", item.rating)
            intent.putExtra("EXTRA_ID", item.serverId)
            intent.putExtra("EXTRA_DESCRIPTION", item.description)
            intent.putExtra("EXTRA_CATEGORY", item.category)
            intent.putExtra("EXTRA_STATUS", item.status)
            intent.putExtra("EXTRA_OWNER", item.owner)
            intent.putExtra("EXTRA_FACILITIES", item.facilities)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size
}
