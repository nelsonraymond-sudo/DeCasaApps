package com.example.decasaapps

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(private val days: List<String?>) :
    RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_date, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dateText = days[position]
        holder.tvDate.text = dateText ?: ""

        // Reset default style
        holder.tvDate.setTextColor(Color.parseColor("#666666"))
        holder.tvDate.setBackgroundColor(Color.TRANSPARENT)

        if (dateText != null) {
            val date = dateText.toIntOrNull()
            if (date != null) {
                // Logic based on design screenshot highlights
                /*
                   Based on image:
                   6, 7, 8 -> Yellow (Rented)
                   24, 25, 26 -> Green (Booked)
                 */
                when (date) {
                    6, 7, 8 -> {
                        holder.tvDate.setTextColor(Color.parseColor("#E8A918")) // Yellow Text? Or bg?
                        // Design shows TEXT is yellow for Rented? Wait "Has been Rented" legend has yellow box beside it.
                        // But in the grid, the numbers 6, 7, 8 are Yellow. The background seems white/transparent.
                        // Let's assume text color is the indicator as per the screenshot where numbers are colored.
                        holder.tvDate.setTextColor(Color.parseColor("#FFC107"))
                    }
                    24, 25, 26 -> {
                        holder.tvDate.setTextColor(Color.parseColor("#9ACD65")) // Green Text
                    }
                    else -> {
                        // Default
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = days.size
}
