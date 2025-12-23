package com.example.decasaapps

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "property_table")
data class PropertyData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @SerializedName("name")
    val name: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("rating")
    val rating: Float,
    @SerializedName("image_url")
    val imageUrl: String
)