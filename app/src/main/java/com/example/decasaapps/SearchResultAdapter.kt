package com.example.decasaapps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.decasaapps.model.PropertyData

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
        holder.tvTitle.text = property.namaProperti
        holder.tvLocation.text = property.alamat
        holder.tvPrice.text = property.harga

        // Karena model tidak punya 'type', kita pakai deskripsi atau string kosong
        holder.tvType.text = property.deskripsi ?: "Property"

        // PERBAIKAN: Load image dari URL String
        Glide.with(holder.itemView.context)
            .load(property.fotoUrl)
            .placeholder(R.drawable.kamar_mewah) // Pastikan gambar ini ada di drawable
            .error(R.drawable.ic_launcher_background) // Gambar cadangan jika error
            .into(holder.ivProperty)
    }

    override fun getItemCount(): Int = propertyList.size
}