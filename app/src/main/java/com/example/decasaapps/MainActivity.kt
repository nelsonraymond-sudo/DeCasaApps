package com.example.decasaapps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.decasaapps.network.ApiClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // RecyclerView
    private lateinit var rvPopular: RecyclerView
    private lateinit var rvRecommended: RecyclerView

    // Views for navigation toggling
    private lateinit var headerLayout: LinearLayout
    private lateinit var scrollView: NestedScrollView
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var bottomNavigationView: BottomNavigationView

    // ImageView untuk notifikasi
    private lateinit var ivNotifikasi: ImageView

    // LinearLayout untuk Search Button
    private lateinit var btnSearch: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // 1. Sambungkan Variabel ke XML
        setupViews()

        // 2. Setup Click Listener untuk Notifikasi
        setupNotificationClick()

        // 3. Setup Click Listener untuk Search
        setupSearchClick()

        // 4. Setup Bottom Navigation
        setupBottomNavigation()

        // 5. Panggil Data API
        fetchDataFromApi()

        // 5. Atur Padding System Bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViews() {
        // RecyclerView
        rvPopular = findViewById(R.id.rvPopular)
        rvRecommended = findViewById(R.id.rvRecommended)

        // ImageView Notifikasi (sesuaikan dengan ID di XML Anda)
        ivNotifikasi = findViewById(R.id.ivNotification)

        // Navigation Views
        headerLayout = findViewById(R.id.headerLayout)
        scrollView = findViewById(R.id.scrollView)
        fragmentContainer = findViewById(R.id.fragmentContainer)
        bottomNavigationView = findViewById(R.id.bottomNavigation)

        // LinearLayout Search Button
        btnSearch = findViewById(R.id.btnSearch)

        // Setup Filter Click
        findViewById<ImageView>(R.id.ivFilter).setOnClickListener {
            loadFragment(FilterFragment())
        }

        // Setting Layout Manager
        rvPopular.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        rvPopular.isNestedScrollingEnabled = false

        rvRecommended.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        rvRecommended.isNestedScrollingEnabled = false

        // Setup Category Clicks
        setupCategoryClicks()
    }

    private fun setupCategoryClicks() {
        val categoryIds = mapOf(
            // R.id.btnCategoryHouse to "House", // Removed to handle separately
            R.id.btnCategoryApartment to "Apartment",
            R.id.btnCategoryVilla to "Villa",
            R.id.btnCategoryCosts to "Costs"
        )

        // Handle House Click Separately
        findViewById<LinearLayout>(R.id.btnCategoryHouse).setOnClickListener {
            val intent = Intent(this, HouseActivity::class.java)
            startActivity(intent)
        }

        for ((id, query) in categoryIds) {
            findViewById<LinearLayout>(id).setOnClickListener {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("QUERY", query)
                startActivity(intent)
            }
        }
    }

    private fun setupNotificationClick() {
        ivNotifikasi.setOnClickListener {
            // Pindah ke NotifikasiFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, NotifikasiFragment())
                .addToBackStack(null) // Agar bisa kembali dengan tombol back
                .commit()
        }
    }

    private fun setupSearchClick() {
        btnSearch.setOnClickListener {
            // Pindah ke SearchInputActivity
            val intent = Intent(this, SearchInputActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    showHome()
                    true
                }
                R.id.nav_chat -> {
                    loadFragment(ChatFragment())
                    true
                }
                R.id.nav_history -> {
                    loadFragment(HistoryFragment())
                    true
                }
                R.id.nav_favorites -> {
                    loadFragment(FavoriteFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun showHome() {
        headerLayout.visibility = View.VISIBLE
        scrollView.visibility = View.VISIBLE
        fragmentContainer.visibility = View.GONE
    }

    private fun loadFragment(fragment: Fragment) {
        headerLayout.visibility = View.GONE
        scrollView.visibility = View.GONE
        fragmentContainer.visibility = View.VISIBLE
        
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun fetchDataFromApi() {
        lifecycleScope.launch {
            try {
                val responseList = ApiClient.instance.getAllProperties()

                if (responseList.isNotEmpty()) {
                    rvPopular.adapter = PropertyAdapter(responseList)
                    rvRecommended.adapter = PropertyAdapter(responseList)
                } else {
                    Toast.makeText(this@MainActivity, "Data Kosong", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("API_ERROR", "Error: ${e.message}")
                Toast.makeText(this@MainActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}