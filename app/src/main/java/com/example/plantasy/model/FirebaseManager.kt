package com.example.plantasy.model

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import android.content.Context
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

class FirebaseManager(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registerUser(
        user: User,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val userWithId = user.copy(id = userId)
                        saveToFirebase("users", userWithId.id, userWithId, onSuccess, onFailure)
                    } else {
                        onFailure("Kullanıcı ID alınamadı.")
                    }
                } else {
                    onFailure("Authentication hatası: ${task.exception?.message}")
                }
            }
    }

    private fun saveToFirebase(
        collection: String,
        documentId: String,
        data: Any,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection(collection)
            .document(documentId)
            .set(data)
            .addOnSuccessListener {
                Toast.makeText(context, "Firestore kaydı başarılı", Toast.LENGTH_SHORT).show()
                onSuccess("Kayıt başarılı")
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Firestore kaydı başarısız", Toast.LENGTH_SHORT).show()
                onFailure("Firestore kaydı başarısız: ${e.message}")
            }
    }

    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Giriş başarılı", Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    Toast.makeText(context, "Giriş başarısız: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun getPlants(onResult: (List<Plant>) -> Unit) {
        firestore.collection("plantss")
            .orderBy("name", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                val plants = mutableListOf<Plant>()
                for (document in result) {
                    val plant = document.toObject(Plant::class.java)
                    plants.add(plant)
                }
                onResult(plants)
            }
            .addOnFailureListener { exception ->
            }
    }

    fun getPlantById(plantId: String, onResult: (Plant?) -> Unit) {
        firestore.collection("plantss")
            .document(plantId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val plant = document.toObject(Plant::class.java)
                    onResult(plant)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                onResult(null)
            }
    }

    fun getUserNameById(userId: String, callback: (String?) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val userName = document.getString("username")
                callback(userName)
            }
            .addOnFailureListener { exception ->
                callback(null)
            }
    }

    fun addCommentToPlant(plantId: String, comment: Comment, onSuccess: () -> Unit) {
        firestore.collection("plantss")
            .document(plantId)
            .update("comments", FieldValue.arrayUnion(comment))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
            }
    }

    fun getUserFavorites(userId: String, onResult: (List<String>) -> Unit) {
        val userDoc = FirebaseFirestore.getInstance().collection("users").document(userId)
        userDoc.get().addOnSuccessListener { document ->
            val favorites = document.get("favorites") as? List<String> ?: emptyList()
            onResult(favorites)
        }
    }

    fun addFavorite(userId: String, plantId: String, onComplete: (Boolean) -> Unit) {
        val userDoc = FirebaseFirestore.getInstance().collection("users").document(userId)
        userDoc.update("favorites", FieldValue.arrayUnion(plantId))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun removeFavorite(userId: String, plantId: String, onComplete: (Boolean) -> Unit) {
        val userDoc = FirebaseFirestore.getInstance().collection("users").document(userId)
        userDoc.update("favorites", FieldValue.arrayRemove(plantId))
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getUserFavoritesPlants(userId: String, callback: (List<Plant>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("users").document(userId)

        docRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val favoritesList = document.get("favorites") as? List<String> ?: emptyList()
                val plantList = mutableListOf<Plant>()

                favoritesList.forEach { plantId ->
                    db.collection("plantss").document(plantId).get().addOnSuccessListener { plantDoc ->
                        if (plantDoc.exists()) {
                            val plant = plantDoc.toObject(Plant::class.java)
                            plant?.let { plantList.add(it) }
                            callback(plantList)
                        }
                    }
                }
            }
        }.addOnFailureListener {
        }
    }

    suspend fun addCompletedDay(userId: String, day: Int): Boolean {
        return try {
            val userRef = firestore.collection("users").document(userId)
            val document = userRef.get().await()

            if (!document.exists()) {
                userRef.set(mapOf("completedDays" to listOf(day))).await()
            } else {
                userRef.update("completedDays", FieldValue.arrayUnion(day)).await()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getCompletedDays(userId: String): List<Int> {
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            if (document.exists()) {
                (document.get("completedDays") as? List<*>)?.mapNotNull { it as? Long }?.map { it.toInt() } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun removeCompletedDay(userId: String, day: Int): Boolean {
        val userRef = db.collection("users").document(userId)
        val userSnapshot = userRef.get().await()
        val completedDays = userSnapshot.get("completedDays") as? List<Int> ?: emptyList()
        val newCompletedDays = completedDays.filter { it != day }

        if (completedDays.size != newCompletedDays.size) {
            userRef.update("completedDays", newCompletedDays).await()
            return true
        }

        return false
    }


}
