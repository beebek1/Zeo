package com.example.zeo

import com.example.zeo.model.UserModel
import com.example.zeo.local.ExpenseEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class ZeoUnitTest {

    @Test
    fun `user model toMap contains correct keys and values`() {
        val user = UserModel(userId = "123", email = "test@test.com", fullName = "Test User")
        val map = user.toMap()
        
        assertEquals("123", map["userId"])
        assertEquals("test@test.com", map["email"])
        assertEquals("Test User", map["fullName"])
    }

    @Test
    fun `expense entity handles negative amounts correctly`() {
        val expense = ExpenseEntity(
            title = "Coffee",
            amount = -5.50,
            transactionType = "Expense",
            tag = "Food",
            date = 123456789L,
            note = "Morning coffee",
            userId = "user_1"
        )
        assertEquals(-5.50, expense.amount, 0.0)
    }

    @Test
    fun `expense entity correctly stores transaction type`() {
        val income = ExpenseEntity(
            title = "Salary",
            amount = 5000.0,
            transactionType = "Income",
            tag = "Job",
            date = 123456789L,
            note = "Monthly salary",
            userId = "user_1"
        )
        assertEquals("Income", income.transactionType)
    }

    @Test
    fun `user model default values are empty`() {
        val user = UserModel()
        assertEquals("", user.userId)
        assertEquals("", user.email)
        assertEquals("", user.fullName)
    }

    @Test
    fun `expense entity correctly stores and retrieves date`() {
        val timestamp = System.currentTimeMillis()
        val expense = ExpenseEntity(
            title = "Lunch",
            amount = 15.0,
            transactionType = "Expense",
            tag = "Food",
            date = timestamp,
            note = "",
            userId = "user_1"
        )
        assertEquals(timestamp, expense.date)
    }
}
