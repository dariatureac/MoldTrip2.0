package com.example.firebasealgorithmtest

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.annotation.StringRes

class RegionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegionsScreen()
        }
    }
}

@Composable
fun RegionsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.regions_title),
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        RegionRow(
            listOf(
                RegionItem(R.drawable.chisinau, R.string.chisinau_text),
                RegionItem(R.drawable.northern, R.string.northern_text)
            )
        )

        RegionRow(
            listOf(
                RegionItem(R.drawable.southern, R.string.southern_text),
                RegionItem(R.drawable.orhei, R.string.orhei_text)
            )
        )

        RegionRow(
            listOf(
                RegionItem(R.drawable.central, R.string.central_text),
                RegionItem(R.drawable.transnistria, R.string.transnistria_text)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        NextButton()
    }
}

data class RegionItem(val imageRes: Int, @StringRes val labelRes: Int)

@Composable
fun RegionRow(items: List<RegionItem>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            RegionItemCard(item, Modifier.weight(1f))
        }
    }
}

@Composable
fun RegionItemCard(item: RegionItem, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .height(160.dp)
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = stringResource(id = item.labelRes),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)) // Rounded corners for image
        )

        Button(
            onClick = {
                if (item.labelRes == R.string.chisinau_text) {
                    context.startActivity(Intent(context, ChisinauActivity::class.java))
                }
                if (item.labelRes == R.string.northern_text) {
                    context.startActivity(Intent(context, NorthernActivity::class.java))
                }
                if (item.labelRes == R.string.southern_text) {
                    context.startActivity(Intent(context, SouthernActivity::class.java))
                }
                if (item.labelRes == R.string.central_text) {
                    context.startActivity(Intent(context, CentralActivity::class.java))
                }
                if (item.labelRes == R.string.orhei_text) {
                    context.startActivity(Intent(context, OrheiActivity::class.java))
                }
                if (item.labelRes == R.string.transnistria_text) {
                    context.startActivity(Intent(context, TransnistriaActivity::class.java))
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = stringResource(id = item.labelRes),
                fontSize = 12.sp,
                color = Color.Black,
                maxLines = 1
            )
        }
    }
}

@Composable
fun NextButton() {
    val context = LocalContext.current

    Button(
        onClick = {
            // Navigate to MainActivity when "Finish" is clicked
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(50.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.purple),
            contentColor = colorResource(id = R.color.green)
        ),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = "Finish", // Change the button text to "Finish"
            fontSize = 16.sp
        )
    }
}
