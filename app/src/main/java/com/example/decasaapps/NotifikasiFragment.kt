package com.example.decasaapps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class NotifikasiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout fragment_notifikasi
        val view = inflater.inflate(R.layout.fragment_notifikasi, container, false)

        // Setup tombol back (jika ada)
        setupBackButton(view)

        return view
    }

    private fun setupBackButton(view: View) {
        // Cari tombol back di layout (sesuaikan ID dengan layout Anda)
        val btnBack = view.findViewById<ImageView>(R.id.btnBack)

        btnBack?.setOnClickListener {
            // Kembali ke activity sebelumnya
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}