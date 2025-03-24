package com.example.plantasy.pages

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.plantasy.model.Comment
import com.example.plantasy.model.FirebaseManager
import com.example.plantasy.model.Plant
import com.example.plantasy.ui.theme.ButtonBG
import com.google.firebase.auth.FirebaseAuth


@Composable
fun PlantInfo(navController: NavController, plantId: String?) {
    val context = LocalContext.current
    val firebaseManager = FirebaseManager(context)
    val plant = remember { mutableStateOf<Plant?>(null) }
    val commentText = remember { mutableStateOf("") }
    val comments = remember { mutableStateListOf<Comment>() }
    val isFavorite = remember { mutableStateOf(false) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(plantId) {
        plantId?.let {
            firebaseManager.getPlantById(it) { fetchedPlant ->
                plant.value = fetchedPlant
                comments.clear()
                fetchedPlant?.comments?.let { plantComments ->
                    comments.addAll(plantComments)
                }
            }
            userId?.let { uid ->
                firebaseManager.getUserFavorites(uid) { favorites ->
                    isFavorite.value = favorites.contains(it)
                }
            }
        }
    }

    plant.value?.let { currentPlant ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = currentPlant.name,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )

            InformationBox("Description: ${currentPlant.description}")
            InformationBox("Watering Frequency: ${currentPlant.wateringFrequency}")
            InformationBox("Fertilizing Frequency: ${currentPlant.fertilizingFrequency}")
            InformationBox("Likes Wind: ${if (currentPlant.likesWind) "Yes" else "No"}")
            InformationBox("Likes Sun: ${if (currentPlant.likesSun) "Yes" else "No"}")
            InformationBox("Temperature Preference: ${currentPlant.temperaturePreference}")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    userId?.let { uid ->
                        if (isFavorite.value) {
                            firebaseManager.removeFavorite(uid, currentPlant.plantId) {
                                isFavorite.value = false
                            }
                        } else {
                            firebaseManager.addFavorite(uid, currentPlant.plantId) {
                                isFavorite.value = true
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFavorite.value) Color(0xFFEF5350) else Color(0xFF79F57F)
                )

            ) {
                Text(if (isFavorite.value) "Remove from Favorites" else "Add to Favorites")
            }

            Text(
                text = "Comments:",
                style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(comments) { comment ->
                    CommentItem(comment, firebaseManager)
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                TextField(
                    value = commentText.value,
                    onValueChange = { commentText.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    placeholder = { Text("Add a comment...") }
                )

                Button(
                    onClick = {
                        val newComment = Comment(
                            text = commentText.value,
                            commentId = plantId + "_${System.currentTimeMillis()}",
                            timestamp = System.currentTimeMillis(),
                            userId = userId ?: "unknown_user"
                        )
                        firebaseManager.addCommentToPlant(currentPlant.plantId, newComment) {
                            comments.add(newComment)
                            commentText.value = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =  Color(0xFF4CAF50)
                    )
                ) {
                    Text("Send")
                }
            }
        }
    } ?: run {
        Text(
            text = "Loading plant details...",
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CommentItem(comment: Comment, firebaseManager: FirebaseManager) {
    val userName = remember { mutableStateOf("Unknown User") }

    LaunchedEffect(comment.userId) {
        firebaseManager.getUserNameById(comment.userId) { name ->
            userName.value = name ?: "Unknown User"
        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = userName.value,
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = comment.text,
                modifier = Modifier.padding(16.dp),
                style = androidx.compose.ui.text.TextStyle(color = Color.White)
            )
        }
    }
}

@Composable
fun InformationBox(info: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = info,
            modifier = Modifier.padding(16.dp),
            style = androidx.compose.ui.text.TextStyle(color = Color.White)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlantInfoPreview() {
    PlantInfo(navController = rememberNavController(), plantId = "123")
}
