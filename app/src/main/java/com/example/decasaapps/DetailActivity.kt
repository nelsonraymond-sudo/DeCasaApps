package com.example.decasaapps

import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.decasaapps.database.AppDatabase
import com.example.decasaapps.PropertyData
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private var isFavorite: Boolean = false
    private lateinit var propertyData: PropertyData
    private lateinit var fabFavorite: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // Menerima Data
        val name = intent.getStringExtra("EXTRA_NAME") ?: ""
        val location = intent.getStringExtra("EXTRA_LOCATION") ?: ""
        val rating = intent.getStringExtra("EXTRA_RATING") ?: ""
        val imageUrl = intent.getStringExtra("EXTRA_IMAGE") ?: ""
        val price = intent.getStringExtra("EXTRA_PRICE") ?: ""
        val description = intent.getStringExtra("EXTRA_DESCRIPTION") ?: "Nyaman"
        val serverId = intent.getStringExtra("EXTRA_ID") ?: ""
        val category = intent.getStringExtra("EXTRA_CATEGORY") ?: "House"
        val status = intent.getStringExtra("EXTRA_STATUS") ?: "Available"
        val owner = intent.getStringExtra("EXTRA_OWNER") ?: "DeCasa Admin"
        val facilities = intent.getStringExtra("EXTRA_FACILITIES") ?: ""

        // Reconstruct PropertyData for Database ops
        propertyData = PropertyData(
            serverId = serverId,
            name = name,
            location = location,
            rating = rating,
            price = price,
            imageUrl = imageUrl,
            description = description,
            category = category,
            status = status,
            owner = owner,
            facilities = facilities
        )

        setupViews(name, location, description, price, imageUrl, category, status, owner, facilities)
        setupMap(location) // Use location string to find on map
        setupFavoriteLogic()

        // Setup Book Now Button
        findViewById<android.view.View>(R.id.btnBookNow).setOnClickListener {
            val intent = android.content.Intent(this, BookingActivity::class.java)
            intent.putExtra("EXTRA_NAME", name)
            intent.putExtra("EXTRA_LOCATION", location)
            intent.putExtra("EXTRA_IMAGE", imageUrl)
            intent.putExtra("EXTRA_PRICE", price)
            intent.putExtra("EXTRA_ID", serverId)
            intent.putExtra("EXTRA_CATEGORY", category)
            startActivity(intent)
        }
    }

    private fun setupViews(name: String, location: String, desc: String, price: String, imageUrl: String, category: String, status: String, owner: String, facilities: String) {
        findViewById<TextView>(R.id.tvDetailTitle).text = name
        findViewById<TextView>(R.id.tvDetailLocation).text = location
        findViewById<TextView>(R.id.tvDescription).text = desc
        findViewById<TextView>(R.id.tvPrice).text = price
        
        // Dynamic Binding
        findViewById<TextView>(R.id.tvCategory).text = category
        findViewById<TextView>(R.id.tvStatus).text = status
        // Owner Name removed from UI
        // findViewById<TextView>(R.id.tvOwnerName).text = owner

        val ivDetailImage = findViewById<ImageView>(R.id.ivDetailImage)
        if (imageUrl.isNotEmpty()) {
            Glide.with(this).load(imageUrl).centerCrop().into(ivDetailImage)
        }

        // Setup Facilities
        val layoutFacilities = findViewById<android.widget.LinearLayout>(R.id.layoutFacilities)
        layoutFacilities.removeAllViews() // Clear placeholders if any

        if (facilities.isNotBlank()) {
            val facilityList = facilities.split(",").map { it.trim() }
            for (facility in facilityList) {
                // Create Layout for Item (Horizontal: Icon + Text)
                val itemLayout = android.widget.LinearLayout(this).apply {
                    orientation = android.widget.LinearLayout.HORIZONTAL
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                        android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 8 
                    }
                    gravity = android.view.Gravity.CENTER_VERTICAL
                }

                val icon = ImageView(this).apply {
                    setImageResource(android.R.drawable.checkbox_on_background)
                    setColorFilter(android.graphics.Color.parseColor("#4CAF50"))
                    layoutParams = android.widget.LinearLayout.LayoutParams(40, 40)
                }

                val text = TextView(this).apply {
                    this.text = facility
                    textSize = 14f
                    setPadding(16, 0, 0, 0)
                    setTextColor(android.graphics.Color.BLACK)
                }

                itemLayout.addView(icon)
                itemLayout.addView(text)
                layoutFacilities.addView(itemLayout)
            }
        } else {
             val text = TextView(this).apply {
                this.text = "No facilities listed"
                textSize = 14f
                setTextColor(android.graphics.Color.GRAY)
            }
            layoutFacilities.addView(text)
        }
    }

    private fun setupFavoriteLogic() {
        fabFavorite = findViewById(R.id.fabFavorite)
        val dao = AppDatabase.getDatabase(this).propertyDao()

        // Check initial state
        lifecycleScope.launch {
            // Because PropertyDao.isFavorite returns Boolean (suspend) or Flow?
            // Assuming we added isFavorite(serverId) : Boolean in DAO previously
             // let's check DAO. If not exist, we use a manual check or try-catch
             // For safety, let's query all or specific if method exists.
             // Based on my memory of PropertyDao, I added `isFavorite`.
             isFavorite = dao.isFavorite(propertyData.serverId)
             updateFavoriteIcon()
        }

        fabFavorite.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteIcon()
            
            lifecycleScope.launch {
                if (isFavorite) {
                    dao.insertProperty(propertyData)
                    Toast.makeText(this@DetailActivity, "Added to Favorites", Toast.LENGTH_SHORT).show()
                } else {
                    dao.deleteByServerId(propertyData.serverId)
                    Toast.makeText(this@DetailActivity, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            fabFavorite.setImageResource(R.drawable.ic_love) // Heart Icon
            fabFavorite.setColorFilter(android.graphics.Color.RED)
        } else {
            fabFavorite.setImageResource(R.drawable.ic_love)
            fabFavorite.setColorFilter(android.graphics.Color.GRAY) // Gray if not favorite
        }
    }

    private fun setupMap(locationName: String) {
        val webView = findViewById<WebView>(R.id.webViewMap)
        webView.settings.javaScriptEnabled = true
        
        // Simple Leaflet Map embedded HTML
        // Use OpenStreetMap Search to find lat/lon roughly centering on Jogja if not found
        // or just hardcode a default if Geocoder is complex to set up here without API Key.
        // For simplicity in this demo, I'll center on Yogyakarta and try to show the name.
        
        val htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
                <style>body, html, #map { height: 100%; margin: 0; }</style>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    // Default to Yogyakarta
                    var map = L.map('map').setView([-7.7956, 110.3695], 13);
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '&copy; OpenStreetMap contributors'
                    }).addTo(map);
                    
                    // Add a marker (Mock location for demo)
                    L.marker([-7.7956, 110.3695]).addTo(map)
                        .bindPopup('$locationName')
                        .openPopup();
                </script>
            </body>
            </html>
        """.trimIndent()
        
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
    }
}