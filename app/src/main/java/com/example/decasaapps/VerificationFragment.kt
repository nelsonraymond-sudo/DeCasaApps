package com.example.decasaapps

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VerificationFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            val view = inflater.inflate(R.layout.fragment_verification, container, false)
            
            val verifyButton = view.findViewById<MaterialButton>(R.id.verifyButton)
            verifyButton.setOnClickListener { navigateToProfile() }
            
            view
        } catch (e: Exception) {
            android.util.Log.e("VerificationFragment", "Error creating view", e)
            android.widget.Toast.makeText(context, "Error loading screen: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            // Return a fallback view or null to prevent crash, though null might cause issues upstream
            // Better to return an empty FrameLayout or similar if desperate, but reporting error is key.
            // For now, rethrow or return null? If we return null, the activity might handle it or crash differently.
            // Let's return a simple error view
            val errorView = android.widget.TextView(context)
            errorView.text = "Error loading Verification: ${e.message}"
            errorView.setTextColor(android.graphics.Color.RED)
            errorView
        }
    }

    private fun navigateToProfile() {
        try {
            if (isAdded && activity != null) {
                val intent = Intent(requireActivity(), ProfileActivity::class.java)
                // Clear back stack so user can't go back to verification
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            } else {
                android.util.Log.e("VerificationFragment", "Navigation failed: Fragment not attached")
                android.widget.Toast.makeText(context, "Navigation Warning: Fragment not attached", android.widget.Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            android.util.Log.e("VerificationFragment", "Navigation error", e)
            android.widget.Toast.makeText(context, "Error navigating: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VerificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}