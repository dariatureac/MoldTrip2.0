package com.example.firebasealgorithmtest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlaceSelectionScreen(
    selectedPlaces: List<Place>, // Изменён параметр с allPlaces на selectedPlaces
    onPlacesSelected: (List<Place>) -> Unit, // Оставляем для совместимости, но не используем
    onLoadPlaces: () -> Unit // Оставляем, но не используем, если места уже выбраны
) {
    var isLoading by remember { mutableStateOf(false) }

    // Загрузка мест только если список пуст (для обратной совместимости)
    LaunchedEffect(Unit) {
        if (selectedPlaces.isEmpty()) {
            isLoading = true
            onLoadPlaces()
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Selected Places for Route",
            style = MaterialTheme.typography.headlineMedium
        )

        if (isLoading) {
            CircularProgressIndicator()
            Text("Loading place list...")
        } else if (selectedPlaces.isEmpty()) {
            Text("No places selected. Please select places from region screens.")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                itemsIndexed(selectedPlaces) { _, place ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = place.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "ID: ${place.id}",
                                style = MaterialTheme.typography.bodySmall
                            )
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
            Button(
                onClick = {
                    onPlacesSelected(selectedPlaces) // Возвращаем текущий список без изменений
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm Selection (${selectedPlaces.size} places)")
            }
        }
    }
}