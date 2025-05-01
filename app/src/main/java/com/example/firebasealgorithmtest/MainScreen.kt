package com.example.firebasealgorithmtest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen(
    places: List<Place>,
    route: List<Place>,
    routeDistance: Double,
    isLoading: Boolean,
    errorMessage: String?,
    onSelectPlacesClick: () -> Unit,
    onViewRouteOnMapClick: () -> Unit,
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
                onClick = onViewRouteOnMapClick,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                enabled = places.isNotEmpty() // Кнопка активна только если есть выбранные места
            ) {
                Text("Посмотреть маршрут")
            }
        }

        Button(
            onClick = onBuildRouteClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Построить маршрут")
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