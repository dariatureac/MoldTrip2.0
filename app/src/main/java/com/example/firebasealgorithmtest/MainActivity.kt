package com.example.firebasealgorithmtest

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MainActivity : ComponentActivity() {

    private val algorithm = Algorithm()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("FirebaseTest", "MainActivity: onCreate вызван")

        try {
            val database = FirebaseDatabase.getInstance()
            Log.d("FirebaseTest", "FirebaseDatabase инициализирован успешно: ${database.reference.toString()}")
        } catch (e: Exception) {
            Log.e("FirebaseTest", "Ошибка инициализации Firebase: ${e.message}")
        }

        setContent {
            val navController = rememberNavController()
            var allPlaces by remember { mutableStateOf<List<Place>>(emptyList()) }
            var selectedPlaces by remember { mutableStateOf<List<Place>>(emptyList()) }
            var routeList by remember { mutableStateOf<List<Place>>(emptyList()) }
            var routeDistance by remember { mutableStateOf(0.0) }
            var isLoading by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            val coroutineScope = rememberCoroutineScope()

            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    MainScreen(
                        places = selectedPlaces,
                        route = routeList,
                        routeDistance = routeDistance,
                        isLoading = isLoading,
                        errorMessage = errorMessage,
                        onSelectPlacesClick = {
                            navController.navigate("select_places")
                        },
                        onBuildRouteClick = {
                            if (selectedPlaces.size < 2) {
                                errorMessage = "Выберите как минимум 2 места для построения маршрута"
                                return@MainScreen
                            }
                            isLoading = true
                            errorMessage = null
                            coroutineScope.launch {
                                loadDataFromFirebase(
                                    selectedPlaces = selectedPlaces,
                                    onRouteCalculated = { route, distance ->
                                        routeList = route
                                        routeDistance = distance
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                    },
                                    onLoadingFinished = {
                                        isLoading = false
                                    }
                                )
                            }
                        }
                    )
                }
                composable("select_places") {
                    PlaceSelectionScreen(
                        allPlaces = allPlaces,
                        onPlacesSelected = { newSelectedPlaces ->
                            selectedPlaces = newSelectedPlaces
                            navController.popBackStack()
                        },
                        onLoadPlaces = {
                            coroutineScope.launch {
                                loadPlacesFromFirebase(
                                    onPlacesLoaded = { places ->
                                        allPlaces = places
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    private suspend fun loadPlacesFromFirebase(
        onPlacesLoaded: (List<Place>) -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d("FirebaseTest", "Загрузка мест из Firebase")
        val database = FirebaseDatabase.getInstance()
        val locationsRef = database.reference.child("locations")

        locationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(locationsSnapshot: DataSnapshot) {
                if (!locationsSnapshot.exists()) {
                    onError("Узел locations пуст")
                    return
                }

                Log.d("FirebaseTest", "Данные в locations найдены, количество записей: ${locationsSnapshot.childrenCount}")
                val places = mutableListOf<Place>()
                for (placeSnapshot in locationsSnapshot.children) {
                    val key = placeSnapshot.key ?: continue
                    Log.d("FirebaseTest", "Обрабатываем место с ключом: $key, значение: ${placeSnapshot.value}")
                    val place = placeSnapshot.getValue(Place::class.java)
                    if (place == null) {
                        Log.e("FirebaseTest", "Не удалось преобразовать: ${placeSnapshot.value}")
                        continue
                    }
                    place.id = placeSnapshot.key?.toLong() ?: 0
                    if (place.name.isNotEmpty()) {
                        places.add(place)
                    } else {
                        Log.e("FirebaseTest", "Место без имени: $place")
                    }
                }

                if (places.isEmpty()) {
                    onError("Список мест пуст")
                    return
                }

                Log.d("FirebaseTest", "Загружено мест: ${places.size}")
                places.forEach { place ->
                    Log.d("FirebaseTest", "Место: ${place.name}, ID: ${place.id}, Address: ${place.address}, Lat: ${place.latitude}, Lon: ${place.longitude}")
                }

                onPlacesLoaded(places.sortedBy { it.id })
            }

            override fun onCancelled(error: DatabaseError) {
                onError("Ошибка чтения locations: ${error.message}")
            }
        })
    }

    private suspend fun loadAllPlaces(): Int = suspendCancellableCoroutine { continuation ->
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

                continuation.resume(places.size)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }

    private suspend fun loadDistances(totalPlaces: Int): Map<String, Double> = suspendCancellableCoroutine { continuation ->
        val database = FirebaseDatabase.getInstance()
        val distancesRef = database.reference.child("distances")

        distancesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(distancesSnapshot: DataSnapshot) {
                if (!distancesSnapshot.exists()) {
                    continuation.resumeWithException(IllegalStateException("Узел distances пуст"))
                    return
                }

                Log.d("FirebaseTest", "Данные в distances найдены, количество записей: ${distancesSnapshot.childrenCount}")
                val distances = mutableMapOf<String, Double>()
                for (distanceSnapshot in distancesSnapshot.children) {
                    val key = distanceSnapshot.key ?: continue
                    Log.d("FirebaseTest", "Обрабатываем ключ: $key, значение: ${distanceSnapshot.value}, тип: ${distanceSnapshot.value?.javaClass?.simpleName}")

                    val firstId = key.toIntOrNull()
                    if (firstId == null || firstId < 1 || firstId > totalPlaces) {
                        Log.e("FirebaseTest", "Некорректный ключ: $key (ожидается число от 1 до $totalPlaces)")
                        continue
                    }

                    when (val rawValue = distanceSnapshot.value) {
                        is List<*> -> {
                            val distancesList = rawValue
                            if (distancesList.size < totalPlaces) {
                                Log.e("FirebaseTest", "Массив для ключа $key имеет недостаточный размер: ${distancesList.size} (ожидается $totalPlaces)")
                                continue
                            }

                            if (distancesList.size > totalPlaces) {
                                Log.w("FirebaseTest", "Массив для ключа $key имеет размер ${distancesList.size}, ожидается $totalPlaces. Лишние элементы будут проигнорированы: ${distancesList.subList(totalPlaces, distancesList.size)}")
                            }

                            distancesList.take(totalPlaces).forEachIndexed { index, distance ->
                                val secondId = index + 1
                                if (secondId > totalPlaces) {
                                    Log.d("FirebaseTest", "Пропускаем secondId=$secondId, так как превышает общее количество мест ($totalPlaces)")
                                    return@forEachIndexed
                                }

                                if (distance == null) {
                                    Log.d("FirebaseTest", "Пропускаем расстояние от $firstId до $secondId: значение null")
                                    return@forEachIndexed
                                }

                                val distanceValue = when (distance) {
                                    is Double -> distance
                                    is Long -> distance.toDouble()
                                    is Int -> distance.toDouble()
                                    is String -> distance.toDoubleOrNull() ?: run {
                                        Log.e("FirebaseTest", "Некорректное значение в массиве для ключа $key, индекс $index: $distance (невозможно преобразовать в Double)")
                                        return@forEachIndexed
                                    }
                                    else -> {
                                        Log.e("FirebaseTest", "Некорректное значение в массиве для ключа $key, индекс $index: $distance (неподдерживаемый тип)")
                                        return@forEachIndexed
                                    }
                                }

                                val key1 = "${firstId}_$secondId"
                                val key2 = "${secondId}_$firstId"
                                distances[key1] = distanceValue
                                distances[key2] = distanceValue
                                Log.d("FirebaseTest", "Добавлено расстояние: $key1 = $distanceValue")
                                Log.d("FirebaseTest", "Добавлено расстояние: $key2 = $distanceValue")
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
                                    is String -> rawValue.toDoubleOrNull() ?: run {
                                        Log.e("FirebaseTest", "Некорректное значение для ключа $key: $rawValue (невозможно преобразовать в Double)")
                                        return@run null
                                    }
                                    else -> null
                                }
                                if (value != null) {
                                    distances[key1] = value
                                    distances[key2] = value
                                    Log.d("FirebaseTest", "Добавлено расстояние (старый формат): $key1 = $value")
                                    Log.d("FirebaseTest", "Добавлено расстояние (старый формат): $key2 = $value")
                                }
                            } catch (e: Exception) {
                                Log.e("FirebaseTest", "Ошибка обработки значения для ключа $key: ${e.message}")
                                continue
                            }
                        }
                        else -> {
                            Log.e("FirebaseTest", "Некорректное значение для ключа $key: $rawValue (неподдерживаемый тип)")
                            continue
                        }
                    }
                }

                if (distances.isEmpty()) {
                    continuation.resumeWithException(IllegalStateException("Нет данных о расстояниях: не удалось загрузить ни одного значения"))
                    return
                }

                distances.forEach { (key, value) ->
                    Log.d("FirebaseTest", "Distance[$key] = $value")
                }
                continuation.resume(distances)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }

    private suspend fun loadDataFromFirebase(
        selectedPlaces: List<Place>,
        onRouteCalculated: (List<Place>, Double) -> Unit,
        onError: (String) -> Unit,
        onLoadingFinished: () -> Unit
    ) {
        Log.d("FirebaseTest", "Начало загрузки данных для маршрута")
        try {
            // Шаг 1: Загружаем количество мест
            val totalPlaces = loadAllPlaces()
            Log.d("FirebaseTest", "Общее количество мест: $totalPlaces")

            // Шаг 2: Загружаем расстояния
            val distances = loadDistances(totalPlaces)

            // Шаг 3: Строим маршрут
            val start = selectedPlaces.firstOrNull { it.id == 1L } ?: run {
                Log.e("FirebaseTest", "Точка с id 1 не найдена, выбираем первую точку")
                selectedPlaces.first()
            }
            Log.d("FirebaseTest", "Стартовая точка: ${start.name}")

            if (selectedPlaces.size >= 2) {
                val place1 = selectedPlaces[0]
                val place2 = selectedPlaces[1]
                val distance = algorithm.getDistance(place1, place2, distances, selectedPlaces.size)
                Log.d("FirebaseTest", "Расстояние между ${place1.name} (id: ${place1.id}) и ${place2.name} (id: ${place2.id}): $distance км")
            }

            val route = algorithm.findOptimalRoute(selectedPlaces, start, distances)
            Log.d("FirebaseTest", "Оптимальный маршрут: ${route.joinToString(" -> ") { it.name }}")

            val routeDistance = algorithm.calculateRouteDistance(route, distances)
            Log.d("FirebaseTest", "Длина маршрута: $routeDistance км")

            onRouteCalculated(route, routeDistance)
        } catch (e: IllegalStateException) {
            onError("Не удалось построить маршрут: ${e.message}")
            Log.e("FirebaseTest", "Ошибка построения маршрута: ${e.message}")
        } catch (e: Exception) {
            onError("Ошибка алгоритма: ${e.message}")
            Log.e("FirebaseTest", "Ошибка алгоритма: ${e.message}")
        } finally {
            onLoadingFinished()
        }
    }
}

@Composable
fun MainScreen(
    places: List<Place>,
    route: List<Place>,
    routeDistance: Double,
    isLoading: Boolean,
    errorMessage: String?,
    onSelectPlacesClick: () -> Unit,
    onBuildRouteClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Оптимальный маршрут",
            style = MaterialTheme.typography.headlineMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onSelectPlacesClick,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Выбрать места")
            }
            Button(
                onClick = onBuildRouteClick,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Построить маршрут")
            }
        }

        if (isLoading) {
            CircularProgressIndicator()
            Text("Загрузка данных...")
        } else if (errorMessage != null) {
            Text(
                text = "Ошибка: $errorMessage",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        } else if (route.isEmpty()) {
            Text(
                text = if (places.isEmpty()) {
                    "Сначала выберите места для построения маршрута."
                } else {
                    "Маршрут ещё не построен. Нажмите 'Построить маршрут'."
                },
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text("Маршрут (${route.size} точек):", style = MaterialTheme.typography.headlineSmall)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                itemsIndexed(route) { index, place ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${index + 1}. ${place.name}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "ID: ${place.id}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Text(
                                text = "Address: ${place.address}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = "Coords: (${place.latitude}, ${place.longitude})",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
            Text(
                text = "Длина маршрута: $routeDistance км",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}