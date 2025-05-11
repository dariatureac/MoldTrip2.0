package com.example.firebasealgorithmtest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

class SummaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedIds = intent.getIntArrayExtra("selectedItems")?.toSet() ?: emptySet()

        setContent {
            if (selectedIds.isNotEmpty()) {
                SummaryScreen(selectedIds)
            } else {
                NoSelectionsScreen()
            }
        }
    }
}

@Composable
fun SummaryScreen(selectedIds: Set<Int>) {
    val context = LocalContext.current
    val purple = Color(ContextCompat.getColor(context, R.color.purple))
    val green = Color(ContextCompat.getColor(context, R.color.green))
    val black = Color(ContextCompat.getColor(context, R.color.black))
    val white = Color(ContextCompat.getColor(context, R.color.white))

    val selectedSpots = selectedIds.mapNotNull { SpotsRepository.getSpotById(it) }

    val centralSpots = selectedSpots.filter { it.id in 1..5 }
    val southernSpots = selectedSpots.filter { it.id in 6..16 }
    val northernSpots = selectedSpots.filter { it.id in 17..26 }
    val orheiSpots = selectedSpots.filter { it.id in 27..30 }
    val transnistriaSpots = selectedSpots.filter { it.id in 31..35 }
    val chisinauSpots = selectedSpots.filter { it.id in 36..66 }

    BackgroundWrapper {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(purple)
                .padding(16.dp)
        ) {
            Text(
                text = "Selected Spots",
                fontSize = 24.sp,
                color = white,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        RegionSection("Central", centralSpots, black)
        RegionSection("Southern", southernSpots, black)
        RegionSection("Northern", northernSpots, black)
        RegionSection("Orhei", orheiSpots, black)
        RegionSection("Transnistria", transnistriaSpots, black)
        RegionSection("Chisinau", chisinauSpots, black)

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    context.startActivity(Intent(context, RegionsActivity::class.java))
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = purple
                )
            ) {
                Text("Modify", color = Color.White, fontSize = 18.sp)
            }

            Button(
                onClick = {
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = purple
                )
            ) {
                Text("Confirm", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun RegionSection(regionName: String, spots: List<Spot>, textColor: Color) {
    if (spots.isNotEmpty()) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
            Text(
                text = "$regionName Region",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            spots.forEach { spot ->
                Text(
                    text = "• (${spot.id}) ${stringResource(id = spot.textResId)}",
                    fontSize = 16.sp,
                    color = textColor,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun NoSelectionsScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No items were selected",
            fontSize = 20.sp,
            color = Color.Black
        )
    }
}
