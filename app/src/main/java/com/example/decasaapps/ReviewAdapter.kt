package com.example.decasaapps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Review(
    val name: String,
    val time: String,
    val comment: String,
    val imageUrl: Int
)

class ReviewAdapter(private val reviewList: List<Review>) :
    RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvComment: TextView = itemView.findViewById(R.id.tvComment)
        val ivAvatar: android.widget.ImageView = itemView.findViewById(R.id.ivUserAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = reviewList[position]
        holder.tvName.text = review.name
        holder.tvTime.text = review.time
        holder.tvComment.text = review.comment
        holder.ivAvatar.setImageResource(review.imageUrl)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }
}
