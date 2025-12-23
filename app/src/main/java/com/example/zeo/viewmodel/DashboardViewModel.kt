package com.example.zeo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.zeo.model.Transaction
import com.example.zeo.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel : ViewModel() {

    private val repository = TransactionRepository()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    init {
        _transactions.value = repository.getTransactions()
    }

    fun addTransaction(transaction: Transaction) {
        repository.addTransaction(transaction)
        _transactions.value = repository.getTransactions()
    }
}