package com.example.decasaapps

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PropertyAdapter(private val propertyList: List<PropertyData>) :
    RecyclerView.Adapter<PropertyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val propertyImage: ImageView = view.findViewById(R.id.ivProperty)
        val propertyName: TextView = view.findViewById(R.id.tvPropertyName)
        val propertyLocation: TextView = view.findViewById(R.id.tvLocation)
        val ratingText: TextView = view.findViewById(R.id.tvRating)
        val favoriteIcon: ImageView = view.findViewById(R.id.ivFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_property, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val property = propertyList[position]

        // Set Data ke Tampilan
        holder.propertyName.text = property.name
        holder.propertyLocation.text = property.location
        holder.ratingText.text = "⭐ ${property.rating}"

        // Load Gambar pakai Glide
        Glide.with(holder.itemView.context)
            .load(property.imageUrl)
            .centerCrop()
            .into(holder.propertyImage)

        // Fitur Favorite (Toggle Icon)
        var isFavorite = false
        holder.favoriteIcon.setOnClickListener {
            isFavorite = !isFavorite
            holder.favoriteIcon.setImageResource(
                if (isFavorite) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )
        }

        // --- BAGIAN PENTING: KLIK PINDAH KE DETAIL ---
        holder.itemView.setOnClickListener {
            // 1. Siapkan Niat (Intent) mau pindah ke DetailActivity
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)

            // 2. Masukkan data yang mau dikirim (Packing Data)
            intent.putExtra("EXTRA_NAME", property.name)
            intent.putExtra("EXTRA_LOCATION", property.location)
            intent.putExtra("EXTRA_RATING", property.rating.toString())
            intent.putExtra("EXTRA_IMAGE", property.imageUrl)
            // Tambahkan harga jika ada di data kamu: intent.putExtra("EXTRA_PRICE", property.price)

            // 3. Berangkat!
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = propertyList.size
}