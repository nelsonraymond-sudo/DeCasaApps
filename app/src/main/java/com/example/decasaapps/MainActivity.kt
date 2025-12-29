package com.example.decasaapps

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.decasaapps.network.ApiClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    // RecyclerView
    private lateinit var rvPopular: RecyclerView
    private lateinit var rvRecommended: RecyclerView

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

        // 4. Panggil Data API
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

        // LinearLayout Search Button
        btnSearch = findViewById(R.id.btnSearch)

        // Setting Layout Manager
        rvPopular.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        rvPopular.isNestedScrollingEnabled = false

        rvRecommended.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        rvRecommended.isNestedScrollingEnabled = false
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
            // Pindah ke SearchActivity
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
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