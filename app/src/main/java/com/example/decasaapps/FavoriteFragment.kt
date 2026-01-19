package com.example.decasaapps

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.decasaapps.adapter.PropertyAdapter
import com.example.decasaapps.database.AppDatabase
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

    private lateinit var rvFavorites: RecyclerView
    private lateinit var adapter: PropertyAdapter

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
        rvFavorites = view.findViewById(R.id.rvFavorites) // Pastikan ID ini ada di XML

        rvFavorites.layoutManager = LinearLayoutManager(context)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSearch.setOnClickListener {
            // Toast.makeText(context, "Search clicked", Toast.LENGTH_SHORT).show()
             val intent = Intent(context, SearchInputActivity::class.java)
             startActivity(intent)
        }

        observeFavorites()
    }

    private fun observeFavorites() {
        val dao = AppDatabase.getDatabase(requireContext()).propertyDao()
        
        dao.getAllFavorites().observe(viewLifecycleOwner) { favorites ->
            if (favorites.isNullOrEmpty()) {
                // Tampilkan Empty State
                rvFavorites.visibility = View.GONE
                view?.findViewById<View>(R.id.emptyStateView)?.visibility = View.VISIBLE
            } else {
                view?.findViewById<View>(R.id.emptyStateView)?.visibility = View.GONE
                rvFavorites.visibility = View.VISIBLE
                adapter = PropertyAdapter(favorites, initialFavoriteState = true) { property, isFavorite ->
                     // Handle un-favorite langsung dari list favorite
                     lifecycleScope.launch {
                        if (!isFavorite) {
                            dao.deleteByServerId(property.serverId)
                            // Item will automatically disappear because of LiveData observation!
                            Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                        }
                     }
                }
                rvFavorites.adapter = adapter
            }
        }
    }
}
