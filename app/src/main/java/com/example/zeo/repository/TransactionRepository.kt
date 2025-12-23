package com.example.zeo.repository

import com.example.zeo.model.Transaction

class TransactionRepository {
    private val transactions = mutableListOf<Transaction>()

    fun getTransactions(): List<Transaction> {
        return transactions.toList()
    }

    fun addTransaction(transaction: Transaction) {
        transactions.add(transaction)
    }
}