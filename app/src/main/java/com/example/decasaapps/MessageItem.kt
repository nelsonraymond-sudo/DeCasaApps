package com.example.decasaapps

data class MessageItem(
    val message: String = "",
    val senderId: String = "",
    val conversationId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
