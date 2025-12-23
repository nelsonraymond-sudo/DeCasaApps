package com.example.decasaapps

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IklanFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IklanFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_iklan, container, false)

        // Setup next button click listener
        val btnNext = view.findViewById<MaterialButton>(R.id.btn_next)
        btnNext.setOnClickListener {
            // Navigate to MainActivity
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            // Optional: finish the current activity if this fragment is in an activity
            requireActivity().finish()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() = IklanFragment()
    }
}