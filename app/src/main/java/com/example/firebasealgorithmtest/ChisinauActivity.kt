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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext

class ChisinauActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChisinauScreen()
        }
    }
}

@Composable
fun ChisinauScreen() {
    // Enable scrolling for the entire screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())  // Enable vertical scrolling
    ) {
        // Chisinau image and title
        ChisinauImageSection()

        // Parks Section with a button
        SectionWithImageAndButton(
            imageRes = R.drawable.chisinau_parks,
            textRes = R.string.chisinau_parks_text
        )

        // Museums Section with a button
        SectionWithImageAndButton(
            imageRes = R.drawable.chisinau_museums,
            textRes = R.string.chisinau_museums_text
        )

        // Monuments Section with a button
        SectionWithImageAndButton(
            imageRes = R.drawable.chisinau_monuments,
            textRes = R.string.chisinau_monuments_text
        )

        // Churches and Monasteries Section with a button
        SectionWithImageAndButton(
            imageRes = R.drawable.chisinau_churches,
            textRes = R.string.chisinau_churches_text
        )
    }
}

@Composable
fun ChisinauImageSection() {
    // Chisinau Image and Title
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.chisinau),
                contentDescription = stringResource(id = R.string.chisinau_text),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = stringResource(id = R.string.chisinau_text),
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun SectionWithImageAndButton(imageRes: Int, textRes: Int) {
    // Each section with an image and button
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = stringResource(id = textRes),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Button with transparent white background
            Button(
                onClick = {
                    // Handle the button click (can navigate or show more details)
                    when (textRes) {
                        R.string.chisinau_parks_text -> {
                            // Navigate to ChisinauParksActivity
                            context.startActivity(Intent(context, ChisinauParksActivity::class.java))
                        }
                        R.string.chisinau_museums_text -> {
                            // Navigate to ChisinauMuseumsActivity
                            context.startActivity(Intent(context, ChisinauMuseumsActivity::class.java))
                        }
                        R.string.chisinau_monuments_text -> {
                            // Navigate to ChisinauMonumentsActivity
                            context.startActivity(Intent(context, ChisinauMonumentsActivity::class.java))
                        }
                        R.string.chisinau_churches_text -> {
                            // Navigate to ChisinauChurchesActivity
                            context.startActivity(Intent(context, ChisinauChurchesActivity::class.java))
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.5f)  // Semi-transparent white
                )
            ) {
                Text(
                    text = stringResource(id = textRes),
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}