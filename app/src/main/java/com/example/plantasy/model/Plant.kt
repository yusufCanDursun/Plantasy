package com.example.plantasy.model

data class Plant(
    val plantId: String = "",
    val name: String = "",
    val description: String = "",
    val wateringFrequency: String = "",
    val fertilizingFrequency: String = "",
    val likesWind: Boolean = false,
    val likesSun: Boolean = false,
    val temperaturePreference: String = "",
    val comments: List<Comment> = listOf()
) {
    // Parametresiz yapıcı burada işlevsel hale gelir
}
