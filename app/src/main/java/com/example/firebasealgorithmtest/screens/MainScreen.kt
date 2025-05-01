package com.example.firebasealgorithmtest.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Main Screen", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Navigate to Chisinau Screen
        Button(onClick = { navController.navigate("chisinau") }) {
            Text("Go to Chisinau")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigate to Regions Screen
        Button(onClick = { navController.navigate("regions") }) {
            Text("Go to Regions")
        }
    }
}
