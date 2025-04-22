package com.example.firebasealgorithmtest

import android.util.Log

class Algorithm {

    fun getDistance(point1: Place, point2: Place, distances: Map<String, Double>, totalPlaces: Int): Double {
        if (point1.name == point2.name || point1.id == point2.id) return 0.0

        val id1 = point1.id.toInt()
        val id2 = point2.id.toInt()

        // Проверяем оба направления: "i_j" и "j_i"
        val key1 = "${id1}_$id2"
        val key2 = "${id2}_$id1"

        val distance = distances[key1] ?: distances[key2]
        if (distance != null) {
            return distance
        }

        Log.e("Algorithm", "Distance between ${point1.name} (id: ${point1.id}) and ${point2.name} (id: ${point2.id}) not found (keys: $key1, $key2).")
        throw IllegalStateException("Distance between ${point1.name} (id: ${point1.id}) and ${point2.name} (id: ${point2.id}) not found. Please check the 'distances' data in Firebase.")
    }

    fun findOptimalRoute(places: List<Place>, start: Place, distances: Map<String, Double>): List<Place> {
        if (places.isEmpty()) {
            Log.e("Algorithm", "Place list is empty")
            throw IllegalStateException("Place list is empty")
        }

        val totalPlaces = places.size
        val unvisited = places.toMutableList()
        val route = mutableListOf<Place>()
        var current = start

        route.add(current)
        unvisited.removeAll { it.id == current.id }

        while (unvisited.isNotEmpty()) {
            val nearest = unvisited.minByOrNull { place ->
                try {
                    val distance = getDistance(current, place, distances, totalPlaces)
                    distance
                } catch (e: IllegalStateException) {
                    Log.e("Algorithm", "Error finding distance to ${place.name}: ${e.message}")
                    Double.POSITIVE_INFINITY // Для сортировки, но исключение всё равно обработается ниже
                }
            }

            if (nearest == null) {
                Log.e("Algorithm", "No nearest point found")
                throw IllegalStateException("No nearest point found")
            }

            try {
                val distance = getDistance(current, nearest, distances, totalPlaces)
                route.add(nearest)
                current = nearest
                unvisited.remove(nearest)
            } catch (e: IllegalStateException) {
                Log.e("Algorithm", "Cannot add nearest point ${nearest.name}: ${e.message}")
                throw e // Пропускаем и передаём ошибку наверх
            }
        }

        return route
    }

    fun calculateRouteDistance(route: List<Place>, distances: Map<String, Double>): Double {
        if (route.size < 2) {
            Log.d("Algorithm", "Route is too short: ${route.size} points")
            return 0.0
        }

        val totalPlaces = route.size
        var totalDistance = 0.0
        for (i in 0 until route.size - 1) {
            try {
                val distance = getDistance(route[i], route[i + 1], distances, totalPlaces)
                totalDistance += distance
            } catch (e: IllegalStateException) {
                Log.e("Algorithm", "Cannot calculate distance between ${route[i].name} and ${route[i + 1].name}: ${e.message}")
                throw e // Пропускаем и передаём ошибку наверх
            }
        }
        return totalDistance
    }
}