package com.example.firebasealgorithmtest.screens

import android.os.Bundle
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
import androidx.navigation.NavController
import com.example.firebasealgorithmtest.R


@Composable
fun RegionsScreen(navController: NavController) {
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

        // Rows of regions
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

        // Next button under the images
        Spacer(modifier = Modifier.height(16.dp)) // Space between content and button
        NextButton(navController)
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
    Box(
        modifier = modifier
            .height(160.dp) // **Set the height of the image container**
            .clip(RoundedCornerShape(8.dp))
    ) {
        Image(
            painter = painterResource(id = item.imageRes),
            contentDescription = stringResource(id = item.labelRes),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize() // Ensure the image fills its container
        )
        Text(
            text = stringResource(id = item.labelRes),
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(6.dp)
                .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun NextButton(navController: NavController) {
    Button(
        onClick = { navController.navigate("nextDestination") }, // You can modify this route
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(50.dp), // Adjust button height
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = colorResource(id = R.color.purple),
            contentColor = colorResource(id = R.color.green)
        ),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = "Next",
            fontSize = 16.sp
        )
    }
}
