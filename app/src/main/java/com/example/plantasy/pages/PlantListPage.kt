package com.example.plantasy.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.plantasy.model.FirebaseManager
import com.example.plantasy.model.Plant
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController


@Composable
fun PlantListPage(navController: NavController) {
    val context = LocalContext.current
    val firebaseManager = FirebaseManager(context)
    val plants = remember { mutableStateListOf<Plant>() }

    LaunchedEffect(Unit) {
        firebaseManager.getPlants { plantList ->
            plants.clear()
            plants.addAll(plantList)
            plants.sortBy { it.name }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Plants",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black
            )
        }


        PlantListMain(plants, navController)
    }
}

@Composable
fun PlantListMain(plants: List<Plant>, navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(plants) { plant ->
            PlantItem(plant, navController)
        }
    }
}

@Composable
fun PlantItem(plant: Plant, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("plantInfo/${plant.plantId}")
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = plant.name,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF81C784)
                )
            )
        }
    }
}

