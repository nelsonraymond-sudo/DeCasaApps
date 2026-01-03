package com.example.decasaapps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

        // Dummy Data
        loadDummyMessages()

        messageAdapter = MessageAdapter(messageList)
        rvMessages.adapter = messageAdapter
    }

    private fun loadDummyMessages() {
        messageList.add(MessageItem("Halo, ini agent property DeCasa ya?", "10:23", true))
        messageList.add(MessageItem("Halo, selamat datang di DeCasa \uD83D\uDE0A Betul kak, ada yang bisa kami bantu? Apakah Kakak sedang mencari rumah untuk disewa?", "10:24", false))
        messageList.add(MessageItem("Iya kak, mau tanya. Villa di kawasan Jogja masih tersedia nggak untuk tanggal 16-19 Juli?", "10:25", true))
        messageList.add(MessageItem("Baik kak, kami cek dulu ya.\n\uD83D\uDCC5 Tanggal: 16 Juli\n\uD83C\uDFE1 Unit: Villa\nSebentar ya kak...", "10:30", false))
        messageList.add(MessageItem("Villa masih tersedia untuk tanggal tersebut kak, dengan harga sewa:\n\uD83D\uDD39 Harian: Rp1.500.000\n\uD83D\uDD39 Dengan fasilitas tambahan (kolam pribadi & BBQ set): Rp1.800.000\nApakah Kakak ingin booking villa-nya sekarang??", "10:31", false))
        messageList.add(MessageItem("Boleh kak, saya mau booking villanya untuk tanggal 10 Juli ya. Pakai yang lengkap dengan fasilitas tambahan aja ya kak \uD83D\uDE4F", "10:28", true))
        messageList.add(MessageItem("Baik kak, kami catat ya\uD83D\uDE4F", "10:30", false))
    }
}
