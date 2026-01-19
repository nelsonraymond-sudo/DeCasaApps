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
// import com.example.decasaapps.model.PropertyData // Removed invalid import
import com.example.decasaapps.database.AppDatabase // Import Database
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
        
        // Initialize ApiClient token from Session
        val sharedPref = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val token = sharedPref.getString("KEY_TOKEN", null)
        com.example.decasaapps.network.ApiClient.token = token

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
        findViewById<LinearLayout>(R.id.btnCategoryHouse).setOnClickListener {
            val intent = Intent(this, HouseActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnCategoryApartment).setOnClickListener {
            startActivity(Intent(this, AppartmentActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnCategoryVilla).setOnClickListener {
            startActivity(Intent(this, VillaActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.btnCategoryCosts).setOnClickListener {
            startActivity(Intent(this, CostsActivity::class.java))
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
                                serverId = itemServer.idProperti, // idProperti is String (non-null in response but strict check might fail if I changed it to nullable? PropertyResponse had idProperti as String (non-nullable) in my previous ViewFile? No, I changed EVERYTHING to String? No, I checked the diff, I only changed things that were problematic. 
                                // Looking at previous diff: 
                                // idProperti was NOT changed to String?, it remained String.
                                // harga WAS changed to String?.
                                // So only harga needs fixing here based on user error.
                                // But checking the error logs might be safer.
                                // User error says: "actual type is 'kotlin.String?', but 'kotlin.String' was expected" for 'harga'.
                                name = itemServer.namaProperti,
                                location = itemServer.alamat ?: "Alamat tidak tersedia",
                                price = itemServer.harga ?: "0", // FIX: Handle null
                                rating = "4.5",
                                imageUrl = fullFotoUrl,
                                description = itemServer.deskripsi ?: "No Description",
                                category = itemServer.kategori?.nmKategori ?: "No Category",
                                status = itemServer.status ?: "Available",
                                owner = itemServer.namaPemilik ?: "DeCasa Admin",
                                facilities = itemServer.listFasilitas.mapNotNull { it.detail?.nmFasilitas }.joinToString(", ")
                            )
                        }


        // --- SET ADAPTER ---
                        rvPopular.adapter = PropertyAdapter(dataSiapTampil, false) { property, isFavorite ->
                            lifecycleScope.launch {
                                val dao = AppDatabase.getDatabase(this@MainActivity).propertyDao()
                                if (isFavorite) {
                                    dao.insertProperty(property)
                                    Toast.makeText(this@MainActivity, "Added to Favorites", Toast.LENGTH_SHORT).show()
                                } else {
                                    dao.deleteByServerId(property.serverId)
                                    Toast.makeText(this@MainActivity, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        // Untuk recommended, kita acak datanya
                        rvRecommended.adapter = PropertyAdapter(dataSiapTampil.shuffled(), false) { property, isFavorite ->
                             lifecycleScope.launch {
                                val dao = AppDatabase.getDatabase(this@MainActivity).propertyDao()
                                if (isFavorite) {
                                    dao.insertProperty(property)
                                    Toast.makeText(this@MainActivity, "Added to Favorites", Toast.LENGTH_SHORT).show()
                                } else {
                                    dao.deleteByServerId(property.serverId)
                                    Toast.makeText(this@MainActivity, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                    } else {
                        Toast.makeText(this@MainActivity, "Data Kosong", Toast.LENGTH_SHORT).show()
                        // loadDummyData() // Fallback disabled to debug API
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown Error"
                    Log.e("API_ERROR", "Response gagal: $errorBody")
                    
                    // Show full error in a Dialog so user can see what's wrong with Laravel
                    android.app.AlertDialog.Builder(this@MainActivity)
                        .setTitle("Server Error ${response.code()}")
                        .setMessage(errorBody.take(500)) // Take first 500 chars to avoid huge dialog
                        .setPositiveButton("OK", null)
                        .show()
                }
            }

            override fun onFailure(call: Call<PropertyResponse>, t: Throwable) {
                Log.e("API_ERROR", "Koneksi Error: ${t.message}")
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                // loadDummyData()
            }
        })
    }

    private fun loadDummyData() {
        Toast.makeText(this, "Dummy Data Disabled", Toast.LENGTH_SHORT).show()
    }
}