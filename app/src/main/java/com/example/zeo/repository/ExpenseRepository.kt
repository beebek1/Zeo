package com.example.zeo.repository

import com.example.zeo.local.ExpenseDao
import com.example.zeo.local.ExpenseEntity
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    fun getExpenses(userId: String): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpenses(userId)
    }

    suspend fun addExpense(expense: ExpenseEntity) {
        expenseDao.insert(expense)
    }

    suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.update(expense)
    }

    suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.delete(expense)
    }
}