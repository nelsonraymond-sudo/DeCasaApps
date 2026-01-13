package com.example.decasaapps.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.decasaapps.R
import com.example.decasaapps.model.PropertyData

class PropertyAdapter(private var listProperty: List<PropertyData>) :
    RecyclerView.Adapter<PropertyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_property, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listProperty[position]

        // 1. Set Nama
        holder.tvNama.text = item.namaProperti

        // 2. Set Lokasi (Alamat)
        holder.tvAlamat.text = item.alamat

        // 3. Set Rating
        holder.tvRating.text = item.rating

        // 4. Set Harga (SAYA KOMENTARI DULU KARENA DI XML TIDAK ADA)
        // Jika nanti kamu tambah TextView harga di XML, hapus tanda // di bawah ini:
        // holder.tvHarga.text = item.harga

        // 5. Load Gambar
        if (!item.fotoUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.fotoUrl)
                .into(holder.imgProperti)
        }

        // 6. Click Listener -> Pindah ke DetailActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = android.content.Intent(context, com.example.decasaapps.DetailActivity::class.java)
            
            intent.putExtra("EXTRA_NAME", item.namaProperti)
            intent.putExtra("EXTRA_LOCATION", item.alamat)
            intent.putExtra("EXTRA_RATING", item.rating)
            intent.putExtra("EXTRA_IMAGE", item.fotoUrl)
            intent.putExtra("EXTRA_PRICE", item.harga)
            intent.putExtra("EXTRA_ID", item.id)
            
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listProperty.size
    }

    fun setData(newList: List<PropertyData>) {
        listProperty = newList
        notifyDataSetChanged()
    }

    // --- BAGIAN INI SUDAH SAYA SESUAIKAN DENGAN XML KAMU ---
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // ID sesuai dengan item_property.xml
        val tvNama: TextView = itemView.findViewById(R.id.tvPropertyName)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvLocation)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val imgProperti: ImageView = itemView.findViewById(R.id.ivProperty)

        // Harga saya hapus dulu karena di XML belum ada
        // val tvHarga: TextView = itemView.findViewById(R.id.tvPrice)
    }
}