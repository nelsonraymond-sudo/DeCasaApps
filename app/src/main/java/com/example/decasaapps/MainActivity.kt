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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.decasaapps.adapter.PropertyAdapter // Import Adapter
import com.example.decasaapps.client.RetrofitClient // Sesuaikan Client
import com.example.decasaapps.model.PropertyData // Import Model
import com.example.decasaapps.network.ApiService // Sesuaikan Interface
import com.example.decasaapps.model.property.PropertyResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var rvPopular: RecyclerView
    private lateinit var rvRecommended: RecyclerView
    private lateinit var headerLayout: LinearLayout
    private lateinit var scrollView: NestedScrollView
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var ivNotifikasi: ImageView
    private lateinit var btnSearch: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        setupViews()
        setupNotificationClick()
        setupSearchClick()
        setupBottomNavigation()
        fetchDataFromApi()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupViews() {
        rvPopular = findViewById(R.id.rvPopular)
        rvRecommended = findViewById(R.id.rvRecommended)
        ivNotifikasi = findViewById(R.id.ivNotification)
        headerLayout = findViewById(R.id.headerLayout)
        scrollView = findViewById(R.id.scrollView)
        fragmentContainer = findViewById(R.id.fragmentContainer)
        bottomNavigationView = findViewById(R.id.bottomNavigation)
        btnSearch = findViewById(R.id.btnSearch)

        findViewById<ImageView>(R.id.ivFilter).setOnClickListener {
            loadFragment(FilterFragment())
        }

        rvPopular.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvPopular.isNestedScrollingEnabled = false

        rvRecommended.layoutManager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        rvRecommended.isNestedScrollingEnabled = false

        setupCategoryClicks()
    }

    private fun setupCategoryClicks() {
        val categoryIds = mapOf(
            R.id.btnCategoryApartment to "Apartment",
            R.id.btnCategoryVilla to "Villa",
            R.id.btnCategoryCosts to "Costs"
        )

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
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, NotifikasiFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupSearchClick() {
        btnSearch.setOnClickListener {
            val intent = Intent(this, SearchInputActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { showHome(); true }
                R.id.nav_chat -> { loadFragment(ChatFragment()); true }
                R.id.nav_history -> { loadFragment(HistoryFragment()); true }
                R.id.nav_favorites -> { loadFragment(FavoriteFragment()); true }
                R.id.nav_profile -> { loadFragment(ProfileFragment()); true }
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
        // Tampilkan loading jika perlu (opsional)

        val apiService = RetrofitClient.instance.create(ApiService::class.java)

        // Panggil fungsi getProperti() yang mengembalikan Call<PropertyResponse>
        apiService.getProperti().enqueue(object : Callback<PropertyResponse> {
            override fun onResponse(
                call: Call<PropertyResponse>,
                response: Response<PropertyResponse>
            ) {
                if (response.isSuccessful) {
                    val propertyResponse = response.body()
                    val dataDariServer = propertyResponse?.data // Ini List<PropertyItem>

                    if (!dataDariServer.isNullOrEmpty()) {

                        // --- PROSES MAPPING (PENTING!) ---
                        // Kita ubah format dari Server (PropertyItem) ke format UI (PropertyData)
                        val dataSiapTampil = dataDariServer.map { itemServer ->

                            // 1. Ambil URL foto pertama (jika ada)
                            val fotoUrlPath = itemServer.listFoto.firstOrNull()?.urlFoto
                            val fullFotoUrl = if (fotoUrlPath != null) {
                                // Ganti URL Localhost
                                "http://10.0.2.2:8000/storage/" + fotoUrlPath
                            } else {
                                "" // Atau URL gambar placeholder
                            }

                            // 2. Masukkan ke wadah PropertyData
                            PropertyData(
                                id = itemServer.idProperti,
                                namaProperti = itemServer.namaProperti,
                                alamat = itemServer.alamat ?: "Alamat tidak tersedia",
                                harga = "Rp ${itemServer.harga}", // Format harga manual
                                deskripsi = itemServer.deskripsi ?: "",
                                rating = "4.5", // Default rating (karena belum ada di DB)
                                fotoUrl = fullFotoUrl
                            )
                        }

                        // --- SET ADAPTER ---
                        rvPopular.adapter = PropertyAdapter(dataSiapTampil)

                        // Untuk recommended, kita acak datanya
                        rvRecommended.adapter = PropertyAdapter(dataSiapTampil.shuffled())

                    } else {
                        Toast.makeText(this@MainActivity, "Data Kosong", Toast.LENGTH_SHORT).show()
                        loadDummyData() // Fallback ke dummy jika server kosong
                    }
                } else {
                    Log.e("API_ERROR", "Response gagal: ${response.message()}")
                    Toast.makeText(this@MainActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                    loadDummyData()
                }
            }

            override fun onFailure(call: Call<PropertyResponse>, t: Throwable) {
                Log.e("API_ERROR", "Koneksi Error: ${t.message}")
                Toast.makeText(this@MainActivity, "Koneksi Error. Cek Server.", Toast.LENGTH_SHORT).show()
                loadDummyData()
            }
        })
    }



    // --- FUNGSI INI TADI RUSAK, SEKARANG SUDAH DIPERBAIKI ---
    private fun loadDummyData() {
        val dummyList = listOf(
            PropertyData(
                id = "1",
                namaProperti = "Luxury Villa Bali",
                alamat = "Bali, Indonesia",
                rating = "4.8",
                harga = "Rp 1M",
                deskripsi = "Villa Mewah",
                fotoUrl = "https://images.unsplash.com/photo-1580587771525-78b9dba3b91d?w=500"
            ),
            PropertyData(
                id = "2",
                namaProperti = "Modern Apartment",
                alamat = "Jakarta, Indonesia",
                rating = "4.5",
                harga = "Rp 500jt",
                deskripsi = "Apartemen Pusat Kota",
                fotoUrl = "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=500"
            )
        )

        rvPopular.adapter = PropertyAdapter(dummyList)
        rvRecommended.adapter = PropertyAdapter(dummyList.shuffled())
        Toast.makeText(this@MainActivity, "Using Mock Data", Toast.LENGTH_SHORT).show()
    }
}