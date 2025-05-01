package com.example.firebasealgorithmtest

import android.util.Log
import kotlin.math.*

class Algorithm {

    private val EARTH_RADIUS_KM = 6371.0

    private fun calculateDistanceByCoordinates(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    fun getDistance(place1: Place, place2: Place, distances: Map<String, Double>): Double {
        if (place1.id == 0L || place2.id == 0L) {
            val distance = calculateDistanceByCoordinates(
                place1.latitude, place1.longitude,
                place2.latitude, place2.longitude
            )
            Log.d("Algorithm", "Расстояние (по координатам) между ${place1.name} (ID: ${place1.id}) и ${place2.name} (ID: ${place2.id}): $distance км")
            return distance
        }

        val key = "${place1.id}_${place2.id}"
        val reverseKey = "${place2.id}_${place1.id}"
        val distance = distances[key] ?: distances[reverseKey] ?: throw IllegalStateException("Distance between ${place1.id} and ${place2.id} not found")
        Log.d("Algorithm", "Расстояние (из базы) между ${place1.name} (ID: ${place1.id}) и ${place2.name} (ID: ${place2.id}): $distance км")
        return distance
    }

    fun findOptimalRoute(places: List<Place>, start: Place, distances: Map<String, Double>): List<Place> {
        if (places.isEmpty()) return emptyList()
        if (places.size == 1) return listOf(start)

        val placesWithoutStart = places.filter { it.id != start.id }
        if (placesWithoutStart.isEmpty()) return listOf(start)

        val n = placesWithoutStart.size
        val placeList = placesWithoutStart
        val startIndex = -1 // Индекс начальной точки (не включаем её в перестановки)

        // dp[mask][last] хранит минимальное расстояние для подмножества mask, заканчивающегося в last
        val dp = Array(1 shl n) { DoubleArray(n) { Double.MAX_VALUE } }
        // prev[mask][last] хранит предыдущую точку для восстановления маршрута
        val prev = Array(1 shl n) { IntArray(n) { -1 } }

        // Инициализация: расстояние от start до каждой точки
        for (i in 0 until n) {
            dp[1 shl i][i] = getDistance(start, placeList[i], distances)
        }

        // Динамическое программирование
        for (mask in 1 until (1 shl n)) {
            for (last in 0 until n) {
                if (mask and (1 shl last) == 0) continue
                val prevMask = mask xor (1 shl last)
                for (prevLast in 0 until n) {
                    if (prevMask and (1 shl prevLast) == 0) continue
                    val newDist = dp[prevMask][prevLast] + getDistance(placeList[prevLast], placeList[last], distances)
                    if (newDist < dp[mask][last]) {
                        dp[mask][last] = newDist
                        prev[mask][last] = prevLast
                    }
                }
            }
        }

        // Находим минимальное расстояние и конечную точку
        var minDistance = Double.MAX_VALUE
        var lastIndex = -1
        val finalMask = (1 shl n) - 1
        for (i in 0 until n) {
            if (dp[finalMask][i] < minDistance) {
                minDistance = dp[finalMask][i]
                lastIndex = i
            }
        }

        // Восстанавливаем маршрут
        val route = mutableListOf<Place>()
        var currentMask = finalMask
        var currentLast = lastIndex
        while (currentMask > 0) {
            route.add(placeList[currentLast])
            val prevLast = prev[currentMask][currentLast]
            currentMask = currentMask xor (1 shl currentLast)
            currentLast = prevLast
        }

        route.reverse()
        val finalRoute = listOf(start) + route
        Log.d("Algorithm", "Оптимальный маршрут (Held-Karp): ${finalRoute.joinToString(" -> ") { "${it.name} (ID: ${it.id})" }}, Длина: $minDistance км")
        return finalRoute
    }

    fun calculateRouteDistance(route: List<Place>, distances: Map<String, Double>): Double {
        if (route.size < 2) return 0.0
        var totalDistance = 0.0

        for (i in 0 until route.size - 1) {
            val place1 = route[i]
            val place2 = route[i + 1]
            val distance = getDistance(place1, place2, distances)
            totalDistance += distance
            Log.d("Algorithm", "Расстояние между ${place1.name} и ${place2.name}: $distance км")
        }

        Log.d("Algorithm", "Общая длина маршрута: $totalDistance км")
        return totalDistance
    }
}