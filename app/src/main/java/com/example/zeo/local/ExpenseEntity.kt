package com.example.zeo.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val transactionType: String,
    val tag: String,
    val date: Long,
    val note: String,
    val userId: String
)
