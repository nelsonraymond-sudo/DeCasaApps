package com.example.decasaapps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.decasaapps.model.Property

class SearchResultAdapter(private val propertyList: List<Property>) :
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

        holder.tvTitle.text = property.title
        holder.tvLocation.text = property.location
        holder.tvPrice.text = property.price + "/year"
        holder.tvType.text = property.type

        Glide.with(holder.itemView.context)
            .load(property.imageResId) // Assuming local resources for now or URL string
            .placeholder(R.drawable.kamar_mewah)
            .into(holder.ivProperty)
    }

    override fun getItemCount(): Int = propertyList.size
}
