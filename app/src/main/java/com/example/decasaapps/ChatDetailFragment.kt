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
import java.util.Locale

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
                
                
                // Add to Firestore (Offline-first approach)
                db.collection("chats").add(newMessage)
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to send (Offline)", Toast.LENGTH_SHORT).show()
                    }

                // Optimistic UI Update & Bot Trigger
                etMessage.text.clear()
                // We don't manually add to list here because the SnapshotListener will pick up local writes immediately
                simulateAdminReply(db, currentUserId, text)
            }
        }
    }

    private fun simulateAdminReply(db: com.google.firebase.firestore.FirebaseFirestore, conversationId: String, userMessage: String) {
        // Smart Chatbot Logic
        val lowerCaseMsg = userMessage.lowercase(Locale.getDefault())
        
        var replyText = "Maaf, saya adalah asisten virtual. Untuk bantuan lebih lanjut, silakan hubungi Customer Service kami via WhatsApp."
        
        if (lowerCaseMsg.contains("halo") || lowerCaseMsg.contains("hai") || lowerCaseMsg.contains("hi") || lowerCaseMsg.contains("pagi") || lowerCaseMsg.contains("siang") || lowerCaseMsg.contains("malam")) {
            replyText = "Halo! Selamat datang di Layanan Pelanggan DeCasa. Ada yang bisa kami bantu hari ini?"
        } else if (lowerCaseMsg.contains("harga") || lowerCaseMsg.contains("biaya") || lowerCaseMsg.contains("price") || lowerCaseMsg.contains("bayar")) {
            replyText = "Untuk informasi harga properti, silakan cek halaman Detail dari properti yang Anda minati. Harga mulai dari Rp.60.000/Day"
        } else if (lowerCaseMsg.contains("lokasi") || lowerCaseMsg.contains("alamat") || lowerCaseMsg.contains("posisi") || lowerCaseMsg.contains("dimana")) {
            replyText = "Properti kami tersebar di beberapa lokasi Yogyakarta. Cek map di detail properti untuk lokasi tepatnya."
        } else if (lowerCaseMsg.contains("booking") || lowerCaseMsg.contains("sewa") || lowerCaseMsg.contains("pesan") || lowerCaseMsg.contains("rent")) {
            replyText = "Untuk melakukan pemesanan, silakan klik tombol 'Rentals' atau 'Payment' di halaman detail properti."
        } else if (lowerCaseMsg.contains("makasih") || lowerCaseMsg.contains("terima kasih") || lowerCaseMsg.contains("thanks") || lowerCaseMsg.contains("thx")) {
            replyText = "Sama-sama! Senang bisa membantu. Jangan ragu untuk menghubungi kami lagi jika ada pertanyaan lain."
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
                .addOnSuccessListener {
                     // Bot reply sent
                }
                .addOnFailureListener { e ->
                     if (context != null) {
                        Toast.makeText(context, "Bot failed to reply", Toast.LENGTH_SHORT).show()
                     }
                }
        }, 1500) // 1.5 second delay for better UX
    }
}
