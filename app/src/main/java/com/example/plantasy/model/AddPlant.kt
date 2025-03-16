package com.example.plantasy.model

import android.content.Context
import com.example.plantasy.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AddPlant(private val context: Context) {

    private fun getPlantsFromJson(): List<Plant> {
        val jsonString = context.resources.openRawResource(R.raw.real_plants_dataset)
            .bufferedReader().use { it.readText() }
        val plantListType = object : TypeToken<List<Plant>>() {}.type
        return Gson().fromJson(jsonString, plantListType)
    }

    // Firestore'daki bitkileri sil
    fun clearFirestorePlants(onComplete: () -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val plantsCollectionRef = firestore.collection("plantss")

        plantsCollectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            println("Plant ${document.id} deleted successfully!")
                        }
                        .addOnFailureListener { e ->
                            println("Error deleting plant ${document.id}: ${e.message}")
                        }
                }
                // Bitkiler silindikten sonra onComplete callback çağır
                onComplete()
            }
            .addOnFailureListener { e ->
                println("Error getting plants to delete: ${e.message}")
            }
    }

    // Firestore'a bitkileri ekle
    fun addAllPlantsToFirestore(onComplete: () -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        val plantList = getPlantsFromJson()
        val plantsCollectionRef = firestore.collection("plantss")

        plantList.forEach { plant ->
            val plantDocRef = plantsCollectionRef.document(plant.plantId)

            plantDocRef.get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {
                        plantDocRef.set(plant)
                            .addOnSuccessListener {
                                println("Plant ${plant.name} saved successfully!")
                            }
                            .addOnFailureListener { e ->
                                println("Error saving plant ${plant.name}: ${e.message}")
                            }
                    } else {
                        println("Plant ${plant.name} already exists.")
                    }
                }
                .addOnFailureListener { e ->
                    println("Error checking plant ${plant.name}: ${e.message}")
                }
        }

        // Callback'i çağırıyoruz, bitkiler başarıyla eklendikten sonra.
        onComplete()
    }
}

