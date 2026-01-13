package com.example.decasaapps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.decasaapps.model.PropertyData

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

        // 1. PERBAIKAN: Gunakan 'namaProperti' bukan 'title'
        holder.tvTitle.text = item.namaProperti

        // 2. PERBAIKAN: Gunakan 'alamat' bukan 'location'
        // Logika split: Memisahkan alamat jika ada koma (misal: "Tebet, Jakarta")
        val rawLocation = item.alamat
        val locationParts = rawLocation.split(",")

        if (locationParts.size > 1) {
            holder.tvAddress.text = locationParts[0].trim() + ","
            holder.tvCity.text = locationParts[1].trim()
        } else {
            holder.tvAddress.text = rawLocation
            holder.tvCity.text = ""
        }

        // 3. PERBAIKAN: Gunakan 'fotoUrl' (String) dan Glide
        Glide.with(holder.itemView.context)
            .load(item.fotoUrl)
            .placeholder(R.drawable.ic_launcher_background) // Gambar sementara loading
            .error(R.drawable.ic_launcher_background) // Gambar jika url error
            .into(holder.ivThumb)
    }

    override fun getItemCount(): Int = list.size
}