package com.example.decasaapps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

private val ChatItem.name: Any
private val ChatItem.message: Any
private val ChatItem.time: Any
private val ChatItem.avatar: Int

class ChatAdapter(
    private val chatList: List<ChatItem>,
    private val onItemClick: (ChatItem) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatarImageView: ImageView = itemView.findViewById(R.id.avatarImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem = chatList[position]

        holder.nameTextView.text = chatItem.name
        holder.messageTextView.text = chatItem.message
        holder.timeTextView.text = chatItem.time
        holder.avatarImageView.setImageResource(chatItem.avatar)

        // Handle item click
        holder.itemView.setOnClickListener {
            onItemClick(chatItem)
        }
    }

    override fun getItemCount(): Int = chatList.size
}