package com.example.cinechips.model

class Ticket {
    data class SoldTicket(
        var id: String = "",
        var bookingId: String = "",
        var movieTitle: String = "",
        var seats: List<String> = emptyList()
    )
}