package com.example.firebasealgorithmtest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

class NorthernActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NorthernScreen()
        }
    }
}

@Composable
fun NorthernScreen() {
    val context = LocalContext.current
    val purple = Color(ContextCompat.getColor(context, R.color.purple))
    val green = Color(ContextCompat.getColor(context, R.color.green))
    val white = Color(ContextCompat.getColor(context, R.color.white))
    val black = Color(ContextCompat.getColor(context, R.color.black))

    // Fetch spots related to the Northern region from the SpotsRepository
    val items = SpotsRepository.spots.filter { spot ->
        spot.id in 17..26 // Spot IDs for the Northern region (adjust these as necessary)
    }

    BackgroundWrapper {
        // Main image + region title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.northern),  // Change to northern region image
                contentDescription = stringResource(R.string.northern_text),  // Change to northern text resource
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = stringResource(R.string.northern_text),
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Items with checkboxes (use global spot IDs)
        items.forEach { spot ->
            NorthernItemCard(
                spotId = spot.id,  // Pass the global spot ID
                spot = spot,
                purple = purple,
                green = green,
                white = white,
                black = black
            )
        }

        // NEXT Button
        Button(
            onClick = {
                context.startActivity(Intent(context, RegionsActivity::class.java))
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp),
            shape = RoundedCornerShape(50),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = purple
            )
        ) {
            Text(
                text = "Next",
                color = white
            )
        }
    }
}

@Composable
fun NorthernItemCard(spotId: Int, spot: Spot, purple: Color, green: Color, white: Color, black: Color) {
    var checked by remember {
        mutableStateOf(SelectionManager.isSpotSelected(spotId))  // Check if the spot is selected by its global ID
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Image(
            painter = painterResource(id = spot.imageResId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(white)
                .padding(12.dp)
        ) {
            Text(
                text = stringResource(id = spot.textResId),
                fontSize = 18.sp,
                color = black,
                modifier = Modifier.weight(1f)
            )
            Checkbox(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    if (it) SelectionManager.selectSpot(spotId) // Use global spot ID
                    else SelectionManager.unselectSpot(spotId)
                },
                colors = androidx.compose.material3.CheckboxDefaults.colors(
                    checkedColor = black
                )
            )
        }
    }
}
