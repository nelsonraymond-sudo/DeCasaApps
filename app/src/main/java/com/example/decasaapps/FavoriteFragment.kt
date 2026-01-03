package com.example.decasaapps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast

class FavoriteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val btnSearch = view.findViewById<ImageView>(R.id.btnSearch)

        btnBack.setOnClickListener {
            // Handle back navigation if needed, or just pop stack
            parentFragmentManager.popBackStack()
        }

        btnSearch.setOnClickListener {
            Toast.makeText(context, "Search clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
