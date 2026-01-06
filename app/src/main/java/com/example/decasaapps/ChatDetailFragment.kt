package com.example.decasaapps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.Query

class ChatDetailFragment : Fragment() {

    private lateinit var rvMessages: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private val messageList = ArrayList<MessageItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup Back Button
        view.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Setup RecyclerView
        rvMessages = view.findViewById(R.id.rvMessages)
        rvMessages.layoutManager = LinearLayoutManager(context)

        // Initialize Firestore
        val db = Firebase.firestore
        
        // Get Current User ID (Email)
        val sharedPref = requireContext().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val currentUserId = sharedPref.getString("KEY_EMAIL", "Guest") ?: "Guest"

        messageAdapter = MessageAdapter(messageList, currentUserId)
        rvMessages.adapter = messageAdapter

        // Listen for Realtime Updates specific to this user's conversation
        db.collection("chats")
            .whereEqualTo("conversationId", currentUserId) // Filter by conversationId (using userId as simplified convId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    messageList.clear()
                    for (doc in snapshots) {
                        val message = doc.toObject(MessageItem::class.java)
                        messageList.add(message)
                    }
                    messageAdapter.notifyDataSetChanged()
                    if (messageList.isNotEmpty()) {
                        rvMessages.scrollToPosition(messageList.size - 1)
                    }
                }
            }

        // Send Message
        val etMessage = view.findViewById<EditText>(R.id.etMessage)
        view.findViewById<ImageView>(R.id.btnSend).setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                val newMessage = MessageItem(
                    message = text,
                    senderId = currentUserId,
                    conversationId = currentUserId, // Using userId as conversationId for 1-on-1 with Admin
                    timestamp = System.currentTimeMillis()
                )
                
                db.collection("chats").add(newMessage)
                    .addOnSuccessListener {
                        etMessage.text.clear()
                        rvMessages.scrollToPosition(messageList.size - 1)
                        simulateAdminReply(db, currentUserId, text)
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to send", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun simulateAdminReply(db: com.google.firebase.firestore.FirebaseFirestore, conversationId: String, userMessage: String) {
        // Smart Chatbot Logic
        val lowerCaseMsg = userMessage.toLowerCase(java.util.Locale.getDefault())
        
        var replyText = "Terima kasih telah menghubungi kami. Admin akan segera membalas."
        
        if (lowerCaseMsg.contains("halo") || lowerCaseMsg.contains("hai") || lowerCaseMsg.contains("hallo") || lowerCaseMsg.contains("pagi") || lowerCaseMsg.contains("siang")) {
            replyText = "Halo! Selamat datang di DeCasa. Ada yang bisa kami bantu?"
        } else if (lowerCaseMsg.contains("harga") || lowerCaseMsg.contains("biaya") || lowerCaseMsg.contains("pricelist")) {
            replyText = "Untuk informasi harga, silakan cek menu Paket Layanan di halaman utama kami."
        } else if (lowerCaseMsg.contains("lokasi") || lowerCaseMsg.contains("alamat") || lowerCaseMsg.contains("dimana")) {
            replyText = "Kami melayani area Jabodetabek. Kantor kami berlokasi di Jakarta Selatan."
        } else if (lowerCaseMsg.contains("booking") || lowerCaseMsg.contains("pesan")) {
            replyText = "Untuk pemesanan, silakan pilih layanan di menu Home dan ikuti langkah pemesanan."
        }

        // Delayed response from "Admin"
        val handler = android.os.Handler(android.os.Looper.getMainLooper())
        
        handler.postDelayed({
            // Check if fragment is still added
            if (!isAdded) return@postDelayed

            val adminMessage = MessageItem(
                message = replyText,
                senderId = "admin", 
                conversationId = conversationId,
                timestamp = System.currentTimeMillis()
            )
            
            db.collection("chats").add(adminMessage)
                .addOnFailureListener { e ->
                     if (context != null) {
                        Toast.makeText(context, "Admin reply failed: ${e.message}", Toast.LENGTH_LONG).show()
                     }
                }
        }, 1500) // 1.5 second delay for better UX
    }
}
