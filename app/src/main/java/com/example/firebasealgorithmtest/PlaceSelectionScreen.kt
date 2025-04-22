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
    allPlaces: List<Place>,
    onPlacesSelected: (List<Place>) -> Unit,
    onLoadPlaces: () -> Unit
) {
    var selectedPlaces by remember { mutableStateOf(setOf<Place>()) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (allPlaces.isEmpty()) {
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
            text = "Выберите места для маршрута",
            style = MaterialTheme.typography.headlineMedium
        )

        if (isLoading) {
            CircularProgressIndicator()
            Text("Загрузка списка мест...")
        } else if (allPlaces.isEmpty()) {
            Text("Список мест пуст")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                itemsIndexed(allPlaces) { _, place ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
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
                            Checkbox(
                                checked = place in selectedPlaces,
                                onCheckedChange = { checked ->
                                    selectedPlaces = if (checked) {
                                        selectedPlaces + place
                                    } else {
                                        selectedPlaces - place
                                    }
                                }
                            )
                        }
                    }
                }
            }
            Button(
                onClick = {
                    onPlacesSelected(selectedPlaces.toList())
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Подтвердить выбор (${selectedPlaces.size} мест)")
            }
        }
    }
}