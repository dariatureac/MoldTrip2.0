package com.example.firebasealgorithmtest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.colorResource
import androidx.annotation.StringRes

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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Chisinau image and title
        ChisinauImageSection()

        // Parks Section
        SectionWithImageAndText(
            imageRes = R.drawable.chisinau_parks,
            textRes = R.string.chisinau_parks_text
        )

        // Museums Section
        SectionWithImageAndText(
            imageRes = R.drawable.chisinau_museums,
            textRes = R.string.chisinau_museums_text
        )

        // Monuments Section
        SectionWithImageAndText(
            imageRes = R.drawable.chisinau_monuments,
            textRes = R.string.chisinau_monuments_text
        )

        // Churches and Monasteries Section
        SectionWithImageAndText(
            imageRes = R.drawable.chisinau_churches_monasteries,
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
fun SectionWithImageAndText(imageRes: Int, textRes: Int) {
    // Each section with an image and text
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
            Text(
                text = stringResource(id = textRes),
                fontSize = 18.sp,
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
