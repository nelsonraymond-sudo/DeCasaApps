package com.example.decasaapps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatFragment : Fragment() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = ArrayList<ChatItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        chatRecyclerView.layoutManager = LinearLayoutManager(context)

        loadDummyData()

        chatAdapter = ChatAdapter(chatList) { chatItem ->
            // Navigate to ChatDetailFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ChatDetailFragment())
                .addToBackStack(null)
                .commit()
        }
        chatRecyclerView.adapter = chatAdapter
    }

    private fun loadDummyData() {
        chatList.clear()
        chatList.add(ChatItem("Admin DeCasa", "Baik kak, kami catat ya \uD83D\uDE4F", "10.30", R.drawable.logo_decasa)) // Assuming logo_decasa exists
        chatList.add(ChatItem("Agent Apart", "Halo benar dengan Agent apart?", "13.03", R.drawable.ic_person)) // Placeholder
        chatList.add(ChatItem("Agent Apart", "Halo benar dengan Agent apart?", "13.03", R.drawable.ic_person))
        chatList.add(ChatItem("Agent Apart", "Halo benar dengan Agent apart?", "13.03", R.drawable.ic_person))
    }
}