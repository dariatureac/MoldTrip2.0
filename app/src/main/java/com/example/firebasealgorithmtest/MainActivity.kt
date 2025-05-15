package com.example.firebasealgorithmtest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val algorithm = Algorithm()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocation: LatLng? = null

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getUserLocation()
        } else {
            Log.e("MainActivity", "Location permission denied")
            userLocation = LatLng(47.0105, 28.8638) // Заглушка для Кишинёва
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("FirebaseTest", "MainActivity: onCreate вызван")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            getUserLocation()
        }

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

            // Load selected places and calculate route
            LaunchedEffect(Unit) {
                val selectedPlaceIds = intent.getIntArrayExtra("selectedPlaceIds")?.toList() ?: emptyList()
                Log.d("MainActivity", "Received selectedPlaceIds: $selectedPlaceIds")
                if (selectedPlaceIds.isNotEmpty()) {
                    isLoading = true
                    coroutineScope.launch {
                        try {
                            val places = loadPlacesFromFirebase()
                            allPlaces = places
                            val filteredPlaces = places.filter { it.id.toInt() in selectedPlaceIds }
                            val userPlace = if (userLocation != null) {
                                Place(
                                    id = 0L,
                                    name = "Ваше местоположение",
                                    address = "Текущее местоположение",
                                    latitude = userLocation!!.latitude,
                                    longitude = userLocation!!.longitude
                                )
                            } else {
                                null
                            }
                            selectedPlaces = if (userPlace != null) listOf(userPlace) + filteredPlaces else filteredPlaces
                            Log.d("MainActivity", "Selected places: ${selectedPlaces.map { it.id }}")
                            if (selectedPlaces.size >= 2) {
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
                            } else {
                                errorMessage = "Выберите как минимум 2 места для построения маршрута"
                                isLoading = false
                            }
                        } catch (e: Exception) {
                            errorMessage = "Ошибка загрузки мест: ${e.message}"
                            Log.e("MainActivity", "Error loading places: ${e.message}")
                            isLoading = false
                        }
                    }
                } else {
                    errorMessage = "No places selected"
                }
            }

            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator()
                            Text("Calculating route...")
                        } else if (errorMessage != null) {
                            Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
                        } else {
                            // Кнопка "Build Route"
                            Button(
                                onClick = {
                                    if (selectedPlaces.size >= 2) {
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
                                    } else {
                                        errorMessage = "Выберите как минимум 2 места для построения маршрута"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text("Build Route", fontSize = 18.sp)
                            }

                            // Стильный список маршрута в колонку без иконок
                            if (routeList.isNotEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "Route:",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        routeList.forEachIndexed { index, place ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${index + 1}.",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    modifier = Modifier
                                                        .width(24.dp)
                                                        .padding(end = 8.dp)
                                                )
                                                Text(
                                                    text = place.name,
                                                    fontSize = 16.sp,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                            if (index < routeList.size - 1) {
                                                Divider(
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                                    thickness = 1.dp,
                                                    modifier = Modifier.padding(vertical = 4.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Distance: ${String.format("%.2f", routeDistance)} km",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                // Кнопка "View on Map"
                                Button(
                                    onClick = {
                                        navController.navigate("mapScreen")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text("View on Map", fontSize = 18.sp)
                                }
                            }
                        }
                    }
                }
                composable("mapScreen") {
                    MapScreen(
                        userLocation = userLocation,
                        selectedPlaces = selectedPlaces,
                        route = routeList,
                        routeDistance = routeDistance,
                        onSelectPlaces = { newSelectedPlaces ->
                            val updatedPlaces = if (userLocation != null) {
                                val userPlace = Place(
                                    id = 0L,
                                    name = "Ваше местоположение",
                                    address = "Текущее местоположение",
                                    latitude = userLocation!!.latitude,
                                    longitude = userLocation!!.longitude
                                )
                                listOf(userPlace) + newSelectedPlaces
                            } else {
                                newSelectedPlaces
                            }
                            selectedPlaces = updatedPlaces
                        },
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }

    private fun getUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("MainActivity", "Нет разрешения на доступ к местоположению")
            userLocation = LatLng(47.0105, 28.8638)
            return
        }

        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    if (lat == 37.4219983 && lon == -122.084) {
                        Log.w("MainActivity", "Получено местоположение Googleplex, игнорируем")
                        userLocation = LatLng(47.0105, 28.8638)
                    } else {
                        userLocation = LatLng(lat, lon)
                        Log.d("MainActivity", "Последнее известное местоположение: ($lat, $lon)")
                    }
                } else {
                    Log.w("MainActivity", "Последнее известное местоположение недоступно, запрашиваем обновления")
                    requestLocationUpdates()
                }
            }.addOnFailureListener { e ->
                Log.e("MainActivity", "Ошибка получения последнего местоположения: ${e.message}")
                userLocation = LatLng(47.0105, 28.8638)
            }
        } catch (e: SecurityException) {
            Log.e("MainActivity", "Ошибка получения местоположения: ${e.message}")
            userLocation = LatLng(47.0105, 28.8638)
        }
    }

    private fun requestLocationUpdates() {
        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000 // 10 секунд
            fastestInterval = 5000 // 5 секунд
        }

        val locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                super.onLocationResult(locationResult)
                val location = locationResult.lastLocation
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    if (lat == 37.4219983 && lon == -122.084) {
                        Log.w("MainActivity", "Получено местоположение Googleplex, игнорируем")
                        userLocation = LatLng(47.0105, 28.8638)
                    } else {
                        userLocation = LatLng(lat, lon)
                        Log.d("MainActivity", "Местоположение пользователя: ($lat, $lon)")
                    }
                    fusedLocationClient.removeLocationUpdates(this)
                } else {
                    Log.e("MainActivity", "Не удалось получить местоположение, используется заглушка")
                    userLocation = LatLng(47.0105, 28.8638)
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }

            override fun onLocationAvailability(locationAvailability: com.google.android.gms.location.LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
                if (!locationAvailability.isLocationAvailable) {
                    Log.e("MainActivity", "Местоположение недоступно (GPS/Wi-Fi выключены?)")
                    userLocation = LatLng(47.0105, 28.8638)
                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            Log.e("MainActivity", "Ошибка получения местоположения: ${e.message}")
            userLocation = LatLng(47.0105, 28.8638)
        }
    }

    private suspend fun loadPlacesFromFirebase(): List<Place> {
        Log.d("FirebaseTest", "Загрузка мест из Firebase")
        return try {
            val places = FirebaseUtils.loadAllPlaces()
            Log.d("FirebaseTest", "Загружено мест: ${places.size}")
            places.forEach { place ->
                Log.d("FirebaseTest", "Место: ${place.name}, ID: ${place.id}, Address: ${place.address}, Lat: ${place.latitude}, Lon: ${place.longitude}")
            }
            places.sortedBy { it.id }
        } catch (e: Exception) {
            Log.e("FirebaseTest", "Ошибка чтения мест: ${e.message}")
            emptyList()
        }
    }

    private suspend fun loadDataFromFirebase(
        selectedPlaces: List<Place>,
        onRouteCalculated: (List<Place>, Double) -> Unit,
        onError: (String) -> Unit,
        onLoadingFinished: () -> Unit
    ) {
        Log.d("FirebaseTest", "Начало загрузки данных для маршрута")
        try {
            val allPlaces = FirebaseUtils.loadAllPlaces()
            Log.d("FirebaseTest", "Общее количество мест: ${allPlaces.size}")

            val distances = FirebaseUtils.loadDistances(allPlaces.size).toMutableMap()

            if (userLocation == null) {
                throw IllegalStateException("Местоположение пользователя не определено. Пожалуйста, предоставьте доступ к местоположению.")
            }

            // Извлекаем местоположение пользователя как фиксированную стартовую точку
            val userPlace = selectedPlaces.firstOrNull { it.id == 0L }
                ?: throw IllegalStateException("Местоположение пользователя не найдено в списке мест")
            Log.d("FirebaseTest", "Стартовая точка: ${userPlace.name}")

            // Оставшиеся места для оптимизации
            val remainingPlaces = selectedPlaces.filter { it.id != 0L }
            if (remainingPlaces.size < 1) {
                throw IllegalStateException("Выберите хотя бы одно место кроме местоположения")
            }

            // Оптимизируем маршрут для оставшихся мест, начиная с userPlace
            val optimizedRoute = listOf(userPlace) + algorithm.findOptimalRoute(remainingPlaces, userPlace, distances)
            Log.d("FirebaseTest", "Оптимальный маршрут: ${optimizedRoute.joinToString(" -> ") { it.name }}")

            val routeDistance = algorithm.calculateRouteDistance(optimizedRoute, distances)
            Log.d("FirebaseTest", "Длина маршрута: $routeDistance км")

            onRouteCalculated(optimizedRoute, routeDistance)
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