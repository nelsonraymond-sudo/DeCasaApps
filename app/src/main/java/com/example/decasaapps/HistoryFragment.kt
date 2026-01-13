package com.example.decasaapps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.decasaapps.client.RetrofitClient
import com.example.decasaapps.client.Api
import com.example.decasaapps.model.booking.HistoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val rvHistory = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvHistory)
        val layoutEmpty = view.findViewById<android.view.View>(R.id.layoutEmpty)
        
        rvHistory.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        btnBack.setOnClickListener {
            // Logic handled by main navigation usually, but if back stack exists:
            parentFragmentManager.popBackStack()
        }
        
        loadHistory(rvHistory, layoutEmpty)
    }

    private fun loadHistory(rv: androidx.recyclerview.widget.RecyclerView, emptyView: android.view.View) {
        val prefs = requireContext().getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)
        val userId = prefs.getString("KEY_ID", null)

        if (userId == null) {
             rv.visibility = View.GONE
             emptyView.visibility = View.VISIBLE
             return
        }

        val apiService = RetrofitClient.instance.create(Api::class.java)
        apiService.getHistory(userId).enqueue(object : Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>, 
                response: Response<HistoryResponse>
            ) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    val list = body.data.map { item ->
                        HistoryItem(
                            id = item.bookingId,
                            propertyName = item.propertyName,
                            location = item.location,
                            price = item.price,
                            date = parseDateToLong(item.date),
                            imageUrl = item.imageUrl,
                            status = item.status
                        )
                    }.toMutableList()

                    if (list.isNotEmpty()) {
                        list.sortByDescending { it.date }
                        rv.adapter = HistoryAdapter(list)
                        rv.visibility = View.VISIBLE
                        emptyView.visibility = View.GONE
                    } else {
                        rv.visibility = View.GONE
                        emptyView.visibility = View.VISIBLE
                    }
                } else {
                    rv.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                rv.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                android.widget.Toast.makeText(context, "Error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun parseDateToLong(dateStr: String): Long {
         try {
             // Try standard SQL format
             val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
             val date = sdf.parse(dateStr)
             return date?.time ?: 0L
         } catch (e: Exception) {
             try {
                // Try only date format
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                val date = sdf.parse(dateStr)
                return date?.time ?: 0L
             } catch (e2: Exception) {
                return 0L
             }
         }
    }
}
