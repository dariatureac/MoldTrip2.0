package com.example.firebasealgorithmtest

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Place(
    var name: String = "",
    var address: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var id: Long = 0L
)