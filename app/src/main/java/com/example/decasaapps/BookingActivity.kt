package com.example.decasaapps

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BookingActivity : AppCompatActivity() {

    private lateinit var tvMonthName: android.widget.TextView
    private lateinit var rvCalendar: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    
    private var currentCalendar = java.util.Calendar.getInstance()
    private var checkInDate: java.util.Calendar? = null
    private var checkOutDate: java.util.Calendar? = null
    private var bookedDates = mutableListOf<com.example.decasaapps.model.booking.BookedDate>()
    
    private var propertyId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        ensureTokenInitialized()

        // Setup UI References
        tvMonthName = findViewById(R.id.tvMonthName)
        rvCalendar = findViewById(R.id.rvCalendar)
        
        // Setup Back Button
        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Get Extras
        val name = intent.getStringExtra("EXTRA_NAME")
        val location = intent.getStringExtra("EXTRA_LOCATION")
        val imageUrl = intent.getStringExtra("EXTRA_IMAGE")
        val price = intent.getStringExtra("EXTRA_PRICE")
        val category = intent.getStringExtra("EXTRA_CATEGORY")
        propertyId = intent.getStringExtra("EXTRA_ID")

        // Setup Navigation
        findViewById<android.view.View>(R.id.btnPrevMonth).setOnClickListener {
            currentCalendar.add(java.util.Calendar.MONTH, -1)
            updateCalendar()
        }
        findViewById<android.view.View>(R.id.btnNextMonth).setOnClickListener {
            currentCalendar.add(java.util.Calendar.MONTH, 1)
            updateCalendar()
        }

        // Setup Booking Button
        findViewById<android.view.View>(R.id.btnBooking).setOnClickListener {
            if (checkInDate == null || checkOutDate == null) {
                Toast.makeText(this, "Please select check-in and check-out dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val intent = android.content.Intent(this, PaymentMethodActivity::class.java)
            intent.putExtra("EXTRA_NAME", name)
            intent.putExtra("EXTRA_LOCATION", location)
            intent.putExtra("EXTRA_IMAGE", imageUrl)
            intent.putExtra("EXTRA_PRICE", price)
            intent.putExtra("EXTRA_ID", propertyId)
            intent.putExtra("EXTRA_CATEGORY", category)
            intent.putExtra("EXTRA_CHECKIN", formatDate(checkInDate!!))
            intent.putExtra("EXTRA_CHECKOUT", formatDate(checkOutDate!!))
            startActivity(intent)
        }

        // Initialize Calendar
        rvCalendar.layoutManager = GridLayoutManager(this, 7)
        updateCalendar()
        
        // Fetch Booked Dates if ID available
        propertyId?.let { fetchBookedDates(it) }
    }

    private fun updateCalendar() {
        val monthName = currentCalendar.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, java.util.Locale.getDefault())
        val year = currentCalendar.get(java.util.Calendar.YEAR)
        tvMonthName.text = "$monthName $year"

        val days = mutableListOf<com.example.decasaapps.model.booking.CalendarDay>()
        
        val tempCal = currentCalendar.clone() as java.util.Calendar
        tempCal.set(java.util.Calendar.DAY_OF_MONTH, 1)
        val startDayOfWeek = tempCal.get(java.util.Calendar.DAY_OF_WEEK)
        val maxDays = tempCal.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)

        // Add empty slots
        for (i in 1 until startDayOfWeek) {
            days.add(com.example.decasaapps.model.booking.CalendarDay(null, tempCal.get(java.util.Calendar.MONTH), tempCal.get(java.util.Calendar.YEAR)))
        }

        // Add actual days
        for (i in 1..maxDays) {
            val day = com.example.decasaapps.model.booking.CalendarDay(i, tempCal.get(java.util.Calendar.MONTH), tempCal.get(java.util.Calendar.YEAR))
            
            // Set Status logic
            day.status = getDayStatus(i, tempCal.get(java.util.Calendar.MONTH), tempCal.get(java.util.Calendar.YEAR))
            
            days.add(day)
        }

        calendarAdapter = CalendarAdapter(days) { selectedDay ->
            handleDateSelection(selectedDay)
        }
        rvCalendar.adapter = calendarAdapter
    }

    private fun getDayStatus(day: Int, month: Int, year: Int): com.example.decasaapps.model.booking.DayStatus {
        val dateCal = java.util.Calendar.getInstance()
        dateCal.set(year, month, day, 0, 0, 0)
        dateCal.set(java.util.Calendar.MILLISECOND, 0)

        // Check Selection
        if (checkInDate != null && isSameDay(dateCal, checkInDate!!)) return com.example.decasaapps.model.booking.DayStatus.SELECTED_START
        if (checkOutDate != null && isSameDay(dateCal, checkOutDate!!)) return com.example.decasaapps.model.booking.DayStatus.SELECTED_END
        if (checkInDate != null && checkOutDate != null && dateCal.after(checkInDate) && dateCal.before(checkOutDate)) {
            return com.example.decasaapps.model.booking.DayStatus.IN_RANGE
        }

        // Check Booked Dates (API)
        for (booked in bookedDates) {
            val start = parseDate(booked.checkIn)
            val end = parseDate(booked.checkOut)
            if (dateCal.isWithinRange(start, end)) return com.example.decasaapps.model.booking.DayStatus.BOOKED
        }

        return com.example.decasaapps.model.booking.DayStatus.AVAILABLE
    }

    private fun handleDateSelection(day: com.example.decasaapps.model.booking.CalendarDay) {
        if (day.day == null) return
        
        val selectedCal = java.util.Calendar.getInstance()
        selectedCal.set(day.year, day.month, day.day!!, 0, 0, 0)
        selectedCal.set(java.util.Calendar.MILLISECOND, 0)

        if (checkInDate == null || (checkInDate != null && checkOutDate != null)) {
            checkInDate = selectedCal
            checkOutDate = null
        } else if (selectedCal.after(checkInDate)) {
            checkOutDate = selectedCal
        } else {
            checkInDate = selectedCal
        }
        updateCalendar()
    }

    private fun fetchBookedDates(id: String) {
        com.example.decasaapps.network.ApiClient.instance.getBookedDates(id).enqueue(object : retrofit2.Callback<com.example.decasaapps.model.booking.BookedDatesResponse> {
            override fun onResponse(
                call: retrofit2.Call<com.example.decasaapps.model.booking.BookedDatesResponse>,
                response: retrofit2.Response<com.example.decasaapps.model.booking.BookedDatesResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        bookedDates.clear()
                        bookedDates.addAll(it)
                        updateCalendar()
                    }
                }
            }

            override fun onFailure(call: retrofit2.Call<com.example.decasaapps.model.booking.BookedDatesResponse>, t: Throwable) {
                android.util.Log.e("BookingActivity", "Error fetching booked dates", t)
            }
        })
    }

    private fun isSameDay(cal1: java.util.Calendar, cal2: java.util.Calendar): Boolean {
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
               cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }
    
    private fun java.util.Calendar.isWithinRange(start: java.util.Calendar?, end: java.util.Calendar?): Boolean {
        if (start == null || end == null) return false
        return (this.compareTo(start) >= 0 && this.compareTo(end) <= 0)
    }

    private fun formatDate(cal: java.util.Calendar): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(cal.time)
    }

    private fun parseDate(dateStr: String): java.util.Calendar? {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val date = sdf.parse(dateStr)
            val cal = java.util.Calendar.getInstance()
            cal.time = date
            cal
        } catch (e: Exception) { null }
    }

    private fun ensureTokenInitialized() {
        if (com.example.decasaapps.network.ApiClient.token == null) {
            val sharedPref = getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
            com.example.decasaapps.network.ApiClient.token = sharedPref.getString("KEY_TOKEN", null)
            android.util.Log.d("DEBUG_NAV", "BookingActivity: Token initialized from Prefs: ${com.example.decasaapps.network.ApiClient.token != null}")
        }
    }
}
