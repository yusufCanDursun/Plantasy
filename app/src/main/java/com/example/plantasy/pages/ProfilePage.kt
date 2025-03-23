package com.example.plantasy.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.plantasy.model.FirebaseManager
import com.example.plantasy.model.Plant
import com.google.firebase.auth.FirebaseAuth


@Composable
fun ProfilePage(navController: NavController) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val firebaseManager = FirebaseManager(LocalContext.current)
    val favoritePlants = remember { mutableStateListOf<Plant>() }
    val username = remember { mutableStateOf("User") }

    LaunchedEffect(userId) {
        userId?.let { uid ->
            firebaseManager.getUserNameById(uid) { name ->
                username.value = name ?: "User"
            }

            favoritePlants.clear()
            firebaseManager.getUserFavoritesPlants(uid) { plantList ->
                favoritePlants.clear()
                favoritePlants.addAll(plantList)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Başlık
        Text(
            text = "Your Favorite Plants",
            color = Color.Black,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "${username.value}",
            color = Color(0xFF313331),
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal
            ),
            modifier = Modifier.padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(favoritePlants) { plant ->
                PlantItem(plant = plant) {
                    navController.navigate("plantInfo/${plant.plantId}")
                }
            }
        }
    }
}

@Composable
fun PlantItem(plant: Plant, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFB1EEB3)),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    ) {
        Text(
            text = plant.name,
            color = Color.Black,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfilePage(navController = rememberNavController())
}
