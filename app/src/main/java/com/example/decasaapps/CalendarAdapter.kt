package com.example.decasaapps

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(
    private val days: List<com.example.decasaapps.model.booking.CalendarDay>,
    private val onItemClick: (com.example.decasaapps.model.booking.CalendarDay) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_date, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = days[position]
        holder.tvDate.text = day.day?.toString() ?: ""

        // Default style
        holder.tvDate.setTextColor(Color.parseColor("#666666"))
        holder.tvDate.setBackgroundResource(0)
        holder.tvDate.setPadding(0, 0, 0, 0)

        if (day.day != null) {
            holder.itemView.setOnClickListener { onItemClick(day) }
            
            when (day.status) {
                com.example.decasaapps.model.booking.DayStatus.RENTED -> {
                    holder.tvDate.setTextColor(Color.parseColor("#FFC107"))
                }
                com.example.decasaapps.model.booking.DayStatus.BOOKED -> {
                    holder.tvDate.setTextColor(Color.parseColor("#9ACD65"))
                }
                com.example.decasaapps.model.booking.DayStatus.SELECTED_START,
                com.example.decasaapps.model.booking.DayStatus.SELECTED_END -> {
                    holder.tvDate.setTextColor(Color.parseColor("#FFFFFF"))
                    holder.tvDate.setBackgroundResource(R.drawable.bg_selected_date) // Need to create this
                }
                com.example.decasaapps.model.booking.DayStatus.IN_RANGE -> {
                    holder.tvDate.setBackgroundColor(Color.parseColor("#E8F5E9")) // Light green range
                }
                else -> {}
            }
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = days.size
}
