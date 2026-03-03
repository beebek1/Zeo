package com.example.zeo

import com.example.zeo.model.UserModel
import com.example.zeo.local.ExpenseEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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

    @Test
    fun `user model update values works correctly`() {
        val user = UserModel("1", "old@mail.com", "Old Name")
        user.fullName = "New Name"
        user.email = "new@mail.com"
        
        assertEquals("New Name", user.fullName)
        assertEquals("new@mail.com", user.email)
    }

    @Test
    fun `expense entity handles zero amount correctly`() {
        val expense = ExpenseEntity(
            title = "Free Item",
            amount = 0.0,
            transactionType = "Expense",
            tag = "Misc",
            date = 0L,
            note = "Testing zero",
            userId = "u1"
        )
        assertEquals(0.0, expense.amount, 0.0)
    }

    @Test
    fun `user model copy creates a modified duplicate`() {
        val user1 = UserModel("id1", "user@test.com", "User One")
        val user2 = user1.copy(fullName = "User Two")
        
        assertEquals("id1", user2.userId)
        assertEquals("user@test.com", user2.email)
        assertEquals("User Two", user2.fullName)
        assertNotEquals(user1.fullName, user2.fullName)
    }

    @Test
    fun `expense entity handles empty title and note`() {
        val expense = ExpenseEntity(
            title = "",
            amount = 100.0,
            transactionType = "Income",
            tag = "None",
            date = 1L,
            note = "",
            userId = "u1"
        )
        assertEquals("", expense.title)
        assertEquals("", expense.note)
    }

    @Test
    fun `expense entity handles large amounts`() {
        val largeAmount = 1_000_000_000.99
        val expense = ExpenseEntity(
            title = "Business Deal",
            amount = largeAmount,
            transactionType = "Income",
            tag = "Business",
            date = 555L,
            note = "Big deal",
            userId = "ceo_1"
        )
        assertEquals(largeAmount, expense.amount, 0.0)
    }
}
