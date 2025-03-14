package com.example.plantasy.model

data class User (
    var id: String = "",
    var username: String,
    var email: String,
    var password: String,
    var favorites: List<String> = emptyList(),
    val completedDays: List<Int> = emptyList()
)