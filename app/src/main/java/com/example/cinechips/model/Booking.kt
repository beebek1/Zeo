package com.example.cinechips.model

data class Booking(
    var id: String = "",
    var userId: String = "",
    var movieId: String = "",
    var seats: List<String> = emptyList(),
    var totalPrice: Int = 0
)
