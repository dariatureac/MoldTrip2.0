package com.example.firebasealgorithmtest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.colorResource
import kotlinx.coroutines.delay

class FirstScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen {
                // Navigate to RegionsActivity after the delay
                startActivity(Intent(this, RegionsActivity::class.java))
                finish() // Close the FirstScreenActivity
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(3000) // 3-second delay
        onTimeout() // Call the navigation function after the delay
    }

    // Define the font family
    val russoOneFamily = FontFamily(
        Font(R.font.russoone_regular) // Assuming russoone_regular is in the res/font directory
    )

    // UI for the splash screen
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.purple)) // Background color
    ) {
        Text(
            text = "MoldTrip", // App name text
            style = TextStyle(
                color = colorResource(id = R.color.green), // Text color
                fontSize = 90.sp,
                fontFamily = russoOneFamily,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
