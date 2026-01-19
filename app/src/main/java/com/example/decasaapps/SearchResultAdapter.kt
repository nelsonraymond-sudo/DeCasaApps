package com.example.decasaapps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.decasaapps.PropertyData

class SearchResultAdapter(private val propertyList: List<PropertyData>) :
    RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProperty: ImageView = itemView.findViewById(R.id.ivProperty)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvType: TextView = itemView.findViewById(R.id.tvType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val property = propertyList[position]

        // PERBAIKAN: Sesuaikan nama variabel dengan Model PropertyData
        holder.tvTitle.text = property.name
        holder.tvLocation.text = property.location
        holder.tvPrice.text = property.price

        // Karena model tidak punya 'type', kita pakai deskripsi atau string kosong
        holder.tvType.text = property.category

        // PERBAIKAN: Load image dari URL String
        Glide.with(holder.itemView.context)
            .load(property.imageUrl)
            .placeholder(R.drawable.kamar_mewah) // Pastikan gambar ini ada di drawable
            .error(R.drawable.ic_launcher_background) // Gambar cadangan jika error
            .into(holder.ivProperty)

        // Add Click Listener
        holder.itemView.setOnClickListener {
            val intent = android.content.Intent(holder.itemView.context, com.example.decasaapps.DetailActivity::class.java)
            intent.putExtra("EXTRA_NAME", property.name)
            intent.putExtra("EXTRA_LOCATION", property.location)
            intent.putExtra("EXTRA_IMAGE", property.imageUrl)
            intent.putExtra("EXTRA_PRICE", property.price)
            intent.putExtra("EXTRA_RATING", property.rating) // Assuming string or conversion needed if float
            intent.putExtra("EXTRA_ID", property.serverId)
            intent.putExtra("EXTRA_DESCRIPTION", property.description)
            intent.putExtra("EXTRA_CATEGORY", property.category)
            intent.putExtra("EXTRA_STATUS", property.status)
            intent.putExtra("EXTRA_OWNER", property.owner)
            intent.putExtra("EXTRA_FACILITIES", property.facilities)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = propertyList.size
}