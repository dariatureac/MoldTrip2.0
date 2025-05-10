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

class ChisinauParksActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChisinauParksScreen()
        }
    }
}

data class ChisinauParksItem(
    val imageRes: Int,
    val textRes: Int
)

@Composable
fun ChisinauParksScreen() {
    val context = LocalContext.current
    val purple = Color(ContextCompat.getColor(context, R.color.purple))
    val green = Color(ContextCompat.getColor(context, R.color.green))
    val white = Color(ContextCompat.getColor(context, R.color.white))
    val black = Color(ContextCompat.getColor(context, R.color.black))

    val items = listOf(
        ChisinauParksItem(R.drawable.chisinau_parks_1, R.string.chisinau_parks_1_text),
        ChisinauParksItem(R.drawable.chisinau_parks_2, R.string.chisinau_parks_2_text),
        ChisinauParksItem(R.drawable.chisinau_parks_3, R.string.chisinau_parks_3_text),
        ChisinauParksItem(R.drawable.chisinau_parks_4, R.string.chisinau_parks_4_text),
        ChisinauParksItem(R.drawable.chisinau_parks_5, R.string.chisinau_parks_5_text),
        ChisinauParksItem(R.drawable.chisinau_parks_6, R.string.chisinau_parks_6_text),
        ChisinauParksItem(R.drawable.chisinau_parks_7, R.string.chisinau_parks_7_text),
        ChisinauParksItem(R.drawable.chisinau_parks_8, R.string.chisinau_parks_8_text),
        ChisinauParksItem(R.drawable.chisinau_parks_9, R.string.chisinau_parks_9_text),
        ChisinauParksItem(R.drawable.chisinau_parks_10, R.string.chisinau_parks_10_text),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Main image + region title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.chisinau),
                contentDescription = stringResource(R.string.chisinau_text),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = stringResource(R.string.chisinau_text),
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

        items.forEachIndexed { index, item ->
            ChisinauParksItemCard(
                index = index,
                item = item,
                purple = purple,
                green = green,
                white = white,
                black = black
            )
        }

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
                color = green
            )
        }
    }
}

@Composable
fun ChisinauParksItemCard(index: Int, item: ChisinauParksItem, purple: Color, green: Color, white: Color, black: Color) {
    var checked by remember {
        mutableStateOf(SelectionManager.isItemSelected("chisinau", index))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
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
                text = stringResource(id = item.textRes),
                fontSize = 18.sp,
                color = black,
                modifier = Modifier.weight(1f)
            )
            Checkbox(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    if (it) SelectionManager.selectItem("chisinau", index)
                    else SelectionManager.unselectItem("chisinau", index)
                },
                colors = androidx.compose.material3.CheckboxDefaults.colors(
                    checkedColor = black
                )
            )
        }
    }
}
