package com.example.firebasealgorithmtest

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.CameraPosition
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color

@Composable
fun MapScreen(
    userLocation: LatLng?,
    selectedPlaces: List<Place>,
    route: List<Place>,
    routeDistance: Double,
    onSelectPlaces: (List<Place>) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val algorithm = Algorithm()
    var currentSelectedPlaces by remember { mutableStateOf(selectedPlaces) }
    var routePoints by remember { mutableStateOf(route.map { LatLng(it.latitude, it.longitude) }) }
    var routeInfo by remember { mutableStateOf<String?>("Маршрут: ${String.format("%.2f", routeDistance)} км") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val hasLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            userLocation ?: LatLng(47.0, 28.8),
            10f
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Маршрут на карте",
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            onClick = {
                if (currentSelectedPlaces.isNotEmpty() && userLocation != null) {
                    coroutineScope.launch {
                        try {
                            val userPlace = Place(
                                id = 0L,
                                name = "Ваше местоположение",
                                address = "Текущее местоположение",
                                latitude = userLocation.latitude,
                                longitude = userLocation.longitude
                            )
                            val placesWithUser = listOf(userPlace) + currentSelectedPlaces

                            val allPlaces = FirebaseUtils.loadAllPlaces()
                            val distances = FirebaseUtils.loadDistances(allPlaces.size)

                            val newRoute = algorithm.findOptimalRoute(placesWithUser, userPlace, distances)
                            val distance = algorithm.calculateRouteDistance(newRoute, distances)

                            routePoints = newRoute.map { LatLng(it.latitude, it.longitude) }
                            routeInfo = "Маршрут: ${String.format("%.2f", distance)} км"
                        } catch (e: Exception) {
                            errorMessage = "Ошибка построения маршрута: ${e.message}"
                        }
                    }
                } else {
                    Toast.makeText(context, "Нет выбранных мест", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = currentSelectedPlaces.isNotEmpty() && userLocation != null
        ) {
            Text("Обновить маршрут")
        }

        Button(
            onClick = {
                currentSelectedPlaces = emptyList()
                routePoints = emptyList()
                routeInfo = null
                Toast.makeText(context, "Карта очищена", Toast.LENGTH_SHORT).show()
            }
        ) {
            Text("Очистить карту")
        }

        Button(onClick = {
            onSelectPlaces(currentSelectedPlaces)
            onBackClick()
        }) {
            Text("Вернуться (${currentSelectedPlaces.size} мест)")
        }

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                compassEnabled = true,
                myLocationButtonEnabled = true
            )
        ) {
            currentSelectedPlaces.forEach { place ->
                Marker(
                    state = MarkerState(position = LatLng(place.latitude, place.longitude)),
                    title = place.name
                )
            }

            if (routePoints.isNotEmpty()) {
                Polyline(
                    points = routePoints,
                    color = Color.Blue,
                    width = 10f
                )
            }
        }

        routeInfo?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        errorMessage?.let {
            Text(
                text = "Ошибка: $it",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}