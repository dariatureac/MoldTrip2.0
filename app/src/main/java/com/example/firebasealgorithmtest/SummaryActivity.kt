package com.example.firebasealgorithmtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.ui.Alignment


class SummaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedMap = intent.getSerializableExtra("selectedItems") as? HashMap<String, Set<Int>>

        setContent {
            selectedMap?.let {
                if (it.isNotEmpty()) {
                    SummaryScreen(selectedMap = it)
                } else {
                    NoSelectionsScreen() // A Composable that shows "No items were selected"
                }
            }
        }
    }
}

@Composable
fun SummaryScreen(selectedMap: Map<String, Set<Int>>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Summary of Selections", fontSize = 24.sp, color = Color.Black)

        selectedMap.forEach { (region, indices) ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = region.uppercase(), fontSize = 20.sp, color = Color.DarkGray)

            indices.forEach { index ->
                Text(text = "• Item index: $index", fontSize = 16.sp, color = Color.Black)
            }
        }
    }
}

@Composable
fun NoSelectionsScreen() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(
            text = "No items were selected",
            fontSize = 20.sp,
            color = Color.Black
        )
    }
}
