package com.example.firebasealgorithmtest

import android.util.Log
import kotlin.math.*

class Algorithm {

    private val EARTH_RADIUS_KM = 6371.0

    // Расчёт расстояния между двумя точками по координатам (формула Haversine)
    private fun calculateDistanceByCoordinates(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1Rad) * cos(lat2Rad) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = EARTH_RADIUS_KM * c
        Log.d("Algorithm", "Расстояние по координатам: lat1=$lat1, lon1=$lon1, lat2=$lat2, lon2=$lon2, distance=$distance км")
        return distance
    }

    // Получение расстояния между двумя местами
    fun getDistance(place1: Place, place2: Place, distances: Map<String, Double>): Double {
        // Если одно из мест — текущее местоположение (id == 0L), используем формулу Haversine
        if (place1.id == 0L || place2.id == 0L) {
            val distance = calculateDistanceByCoordinates(
                place1.latitude, place1.longitude,
                place2.latitude, place2.longitude
            )
            Log.d("Algorithm", "Расстояние (по координатам) между ${place1.name} (ID: ${place1.id}) и ${place2.name} (ID: ${place2.id}): $distance км")
            return distance
        }

        // Иначе берём расстояние из базы данных (distances)
        val key = "${place1.id}_${place2.id}"
        val reverseKey = "${place2.id}_${place1.id}"
        val distance = distances[key] ?: distances[reverseKey] ?: throw IllegalStateException("Distance between ${place1.id} and ${place2.id} not found in distances map")
        Log.d("Algorithm", "Расстояние (из базы) между ${place1.name} (ID: ${place1.id}) и ${place2.name} (ID: ${place2.id}): $distance км")
        return distance
    }

    // Основной метод для поиска маршрута с использованием Nearest Neighbor и 2-Opt
    fun findOptimalRoute(places: List<Place>, start: Place, distances: Map<String, Double>): List<Place> {
        if (places.isEmpty()) return emptyList()
        if (places.size == 1) return listOf(start)

        val placesWithoutStart = places.filter { it.id != start.id }
        if (placesWithoutStart.isEmpty()) return listOf(start)

        // Шаг 1: Nearest Neighbor для построения начального маршрута
        val initialRoute = nearestNeighbor(placesWithoutStart, start, distances)

        // Шаг 2: Оптимизация маршрута с помощью 2-Opt
        val optimizedRoute = twoOptOptimization(initialRoute, distances)

        // Добавляем начальную точку в начало маршрута
        val finalRoute = listOf(start) + optimizedRoute
        Log.d("Algorithm", "Оптимальный маршрут (Nearest Neighbor + 2-Opt): ${finalRoute.joinToString(" -> ") { "${it.name} (ID: ${it.id})" }}")
        return finalRoute
    }

    // Алгоритм Nearest Neighbor
    private fun nearestNeighbor(places: List<Place>, start: Place, distances: Map<String, Double>): List<Place> {
        val unvisited = places.toMutableList()
        val route = mutableListOf<Place>()
        var current = start

        // Пока есть непосещённые места
        while (unvisited.isNotEmpty()) {
            // Находим ближайшее непосещённое место
            val nearest = unvisited.minByOrNull { getDistance(current, it, distances) } ?: break
            route.add(nearest)
            unvisited.remove(nearest)
            current = nearest
        }

        Log.d("Algorithm", "Маршрут после Nearest Neighbor: ${route.joinToString(" -> ") { "${it.name} (ID: ${it.id})" }}")
        return route
    }

    // Оптимизация 2-Opt
    private fun twoOptOptimization(route: List<Place>, distances: Map<String, Double>): List<Place> {
        var bestRoute = route.toMutableList()
        var bestDistance = calculateRouteDistance(listOf<Place>() + bestRoute, distances) // Без учёта start
        var improved = true

        while (improved) {
            improved = false
            // Перебираем все пары рёбер (i, j)
            for (i in 1 until bestRoute.size - 1) {
                for (j in i + 1 until bestRoute.size) {
                    // Пропускаем, если j - i == 1, так как это соседние точки
                    if (j - i == 1) continue

                    // Создаём новый маршрут, меняя рёбра (i-1, i) и (j-1, j) на (i-1, j-1) и (i, j)
                    val newRoute = twoOptSwap(bestRoute, i, j)
                    val newDistance = calculateRouteDistance(listOf<Place>() + newRoute, distances)

                    // Если новый маршрут короче, обновляем лучший маршрут
                    if (newDistance < bestDistance) {
                        bestRoute = newRoute.toMutableList()
                        bestDistance = newDistance
                        improved = true
                        Log.d("Algorithm", "Улучшение 2-Opt: новый маршрут ${newRoute.joinToString(" -> ") { "${it.name} (ID: ${it.id})" }}, длина: $newDistance км")
                    }
                }
            }
        }

        Log.d("Algorithm", "Маршрут после 2-Opt: ${bestRoute.joinToString(" -> ") { "${it.name} (ID: ${it.id})" }}, длина: $bestDistance км")
        return bestRoute
    }

    // Метод для выполнения 2-Opt обмена
    private fun twoOptSwap(route: List<Place>, i: Int, j: Int): List<Place> {
        val newRoute = mutableListOf<Place>()
        // Копируем часть маршрута до i-1
        for (k in 0 until i) {
            newRoute.add(route[k])
        }
        // Копируем часть от i до j в обратном порядке
        for (k in j downTo i) {
            newRoute.add(route[k])
        }
        // Копируем оставшуюся часть маршрута
        for (k in j + 1 until route.size) {
            newRoute.add(route[k])
        }
        return newRoute
    }

    // Расчёт общей длины маршрута
    fun calculateRouteDistance(route: List<Place>, distances: Map<String, Double>): Double {
        if (route.size < 2) return 0.0
        var totalDistance = 0.0

        for (i in 0 until route.size - 1) {
            val place1 = route[i]
            val place2 = route[i + 1]
            val distance = getDistance(place1, place2, distances)
            totalDistance += distance
            Log.d("Algorithm", "Расстояние между ${place1.name} (ID: ${place1.id}) и ${place2.name} (ID: ${place2.id}): $distance км")
        }

        Log.d("Algorithm", "Общая длина маршрута: $totalDistance км")
        return totalDistance
    }
}