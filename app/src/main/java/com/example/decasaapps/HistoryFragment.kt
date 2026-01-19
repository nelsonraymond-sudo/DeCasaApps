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
        // Call endpoint directly, Auth token handles user identification
        apiService.getHistory().enqueue(object : Callback<com.google.gson.JsonElement> {
            override fun onResponse(
                call: Call<com.google.gson.JsonElement>, 
                response: Response<com.google.gson.JsonElement>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val rawJson = response.body().toString()
                    android.util.Log.d("HISTORY_RAW_JSON", rawJson)
                    
                    try {
                        val jsonElement = com.google.gson.JsonParser.parseString(rawJson)
                        val historyArray = if (jsonElement.isJsonArray) {
                            jsonElement.asJsonArray
                        } else if (jsonElement.isJsonObject && jsonElement.asJsonObject.has("data") && jsonElement.asJsonObject.get("data").isJsonArray) {
                            jsonElement.asJsonObject.get("data").asJsonArray
                        } else {
                            com.google.gson.JsonArray()
                        }

                        val list = mutableListOf<HistoryItem>()
                        for (element in historyArray) {
                            if (element.isJsonObject) {
                                val obj = element.asJsonObject
                                
                                // Core Transaction Fields matching 'Transaksi' model
                                val id = if (obj.has("id_trans")) obj.get("id_trans").asString else if (obj.has("id")) obj.get("id").asString else ""
                                val price = if (obj.has("total_harga")) obj.get("total_harga").asString else if (obj.has("price")) obj.get("price").asString else ""
                                val dateStr = if (obj.has("tgl_trans")) obj.get("tgl_trans").asString else if (obj.has("date")) obj.get("date").asString else ""
                                val status = if (obj.has("status")) obj.get("status").asString else "Success"

                                // Nested Property Fields
                                var name = "Unknown Property"
                                var loc = ""
                                var img = ""

                                if (obj.has("properti") && !obj.get("properti").isJsonNull) {
                                    val propObj = obj.get("properti").asJsonObject
                                    name = if (propObj.has("nm_properti")) propObj.get("nm_properti").asString else name
                                    loc = if (propObj.has("lokasi")) propObj.get("lokasi").asString else loc
                                    
                                    // Handle Foto (could be array or single object depending on relation)
                                    if (propObj.has("foto") && !propObj.get("foto").isJsonNull) {
                                        val fotoElement = propObj.get("foto")
                                        if (fotoElement.isJsonArray && fotoElement.asJsonArray.size() > 0) {
                                           val firstFoto = fotoElement.asJsonArray.get(0).asJsonObject
                                           img = if (firstFoto.has("url_foto")) firstFoto.get("url_foto").asString else ""
                                        }
                                    }
                                }

                                list.add(HistoryItem(
                                    id = id,
                                    propertyName = name,
                                    location = loc,
                                    price = price,
                                    date = parseDateToLong(dateStr),
                                    imageUrl = img,
                                    status = status
                                ))
                            }
                        }

                        if (list.isNotEmpty()) {
                            list.sortByDescending { it.date }
                            rv.adapter = HistoryAdapter(list)
                            rv.visibility = View.VISIBLE
                            emptyView.visibility = View.GONE
                        } else {
                            rv.visibility = View.GONE
                            emptyView.visibility = View.VISIBLE
                            android.util.Log.d("HISTORY", "Empty list parsed")
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("HISTORY_ERROR", "Parse error: ${e.message}")
                        // Only toast debugging info
                        // android.widget.Toast.makeText(context, "History Parse Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                        rv.visibility = View.GONE
                        emptyView.visibility = View.VISIBLE
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("HISTORY_FAIL", "Response Error: $errorBody")
                    rv.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<com.google.gson.JsonElement>, t: Throwable) {
                rv.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                android.widget.Toast.makeText(context, "Connection Error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                android.util.Log.e("HISTORY_FAIL", "Failure: ${t.message}", t)
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
