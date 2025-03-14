package com.example.plantasy.model

data class Comment(
    val text: String = "",
    val commentId: String = "",
    val timestamp: Long = 0L,
    val userId: String = ""
) {
    // Firebase, parametresiz yapıcıyı kullanarak nesneyi oluşturacaktır
    constructor() : this("", "", 0L, "")
}
