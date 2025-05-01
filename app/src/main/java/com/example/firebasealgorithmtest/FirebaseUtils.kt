package com.example.firebasealgorithmtest

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

object FirebaseUtils {

    suspend fun loadAllPlaces(): List<Place> = suspendCancellableCoroutine { continuation ->
        val database = FirebaseDatabase.getInstance()
        val locationsRef = database.reference.child("locations")

        locationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(locationsSnapshot: DataSnapshot) {
                if (!locationsSnapshot.exists()) {
                    continuation.resumeWithException(IllegalStateException("Узел locations пуст"))
                    return
                }

                val places = mutableListOf<Place>()
                for (placeSnapshot in locationsSnapshot.children) {
                    val place = placeSnapshot.getValue(Place::class.java) ?: continue
                    place.id = placeSnapshot.key?.toLong() ?: continue
                    places.add(place)
                }

                if (places.isEmpty()) {
                    continuation.resumeWithException(IllegalStateException("Список мест пуст"))
                    return
                }

                continuation.resume(places)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }

    suspend fun loadDistances(totalPlaces: Int): Map<String, Double> = suspendCancellableCoroutine { continuation ->
        val database = FirebaseDatabase.getInstance()
        val distancesRef = database.reference.child("distances")

        distancesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(distancesSnapshot: DataSnapshot) {
                if (!distancesSnapshot.exists()) {
                    continuation.resumeWithException(IllegalStateException("Узел distances пуст"))
                    return
                }

                val distances = mutableMapOf<String, Double>()
                for (distanceSnapshot in distancesSnapshot.children) {
                    val key = distanceSnapshot.key ?: continue
                    val firstId = key.toIntOrNull()
                    if (firstId == null || firstId < 1 || firstId > totalPlaces) {
                        continue
                    }

                    when (val rawValue = distanceSnapshot.value) {
                        is List<*> -> {
                            val distancesList = rawValue
                            if (distancesList.size < totalPlaces) {
                                continue
                            }

                            distancesList.take(totalPlaces).forEachIndexed { index, distance ->
                                val secondId = index + 1
                                if (secondId > totalPlaces || distance == null) {
                                    return@forEachIndexed
                                }

                                val distanceValue = when (distance) {
                                    is Double -> distance
                                    is Long -> distance.toDouble()
                                    is Int -> distance.toDouble()
                                    is String -> distance.toDoubleOrNull() ?: return@forEachIndexed
                                    else -> return@forEachIndexed
                                }

                                val key1 = "${firstId}_$secondId"
                                val key2 = "${secondId}_$firstId"
                                distances[key1] = distanceValue
                                distances[key2] = distanceValue
                            }
                        }
                        is Double, is Long, is Int, is String -> {
                            val secondId = firstId
                            val firstIdForOldFormat = 1
                            val key1 = "${firstIdForOldFormat}_$secondId"
                            val key2 = "${secondId}_$firstIdForOldFormat"
                            try {
                                val value = when (rawValue) {
                                    is Double -> rawValue
                                    is Long -> rawValue.toDouble()
                                    is Int -> rawValue.toDouble()
                                    is String -> rawValue.toDoubleOrNull()
                                    else -> null
                                }
                                if (value != null) {
                                    distances[key1] = value
                                    distances[key2] = value
                                }
                            } catch (e: Exception) {
                                continue
                            }
                        }
                        else -> continue
                    }
                }

                if (distances.isEmpty()) {
                    continuation.resumeWithException(IllegalStateException("Нет данных о расстояниях"))
                    return
                }

                continuation.resume(distances)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }
}