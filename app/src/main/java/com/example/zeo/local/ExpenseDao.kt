package com.example.zeo.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insert(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getExpenses(userId: String): Flow<List<ExpenseEntity>>

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)
}