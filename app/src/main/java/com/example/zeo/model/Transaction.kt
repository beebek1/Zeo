package com.example.zeo.model

import java.util.Date

enum class TransactionType {
    INCOME, EXPENSE
}

enum class TransactionCategory {
    FOOD, RENT, TRAVEL, SALARY, OTHER
}

data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val type: TransactionType,
    val category: TransactionCategory,
    val date: Date
)