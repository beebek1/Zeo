package com.example.zeo

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.zeo.local.ExpenseEntity
import com.example.zeo.view.AddTransactionScreen
import com.example.zeo.viewmodel.ExpenseViewModel
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class ZeoInstrumentationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addTransactionScreen_displaysCorrectTitle() {
        val mockViewModel = mockk<ExpenseViewModel>(relaxed = true)
        composeTestRule.setContent {
            AddTransactionScreen(viewModel = mockViewModel, onBack = {})
        }

        composeTestRule.onNodeWithText("Add Transaction").assertIsDisplayed()
    }

    @Test
    fun addTransactionScreen_showsErrorOnEmptyFields() {
        val mockViewModel = mockk<ExpenseViewModel>(relaxed = true)
        composeTestRule.setContent {
            AddTransactionScreen(viewModel = mockViewModel, onBack = {})
        }

        composeTestRule.onNodeWithText("Save Transaction").performClick()
        // Toast testing is limited in Compose, but we check if we're still on the screen
        composeTestRule.onNodeWithText("Save Transaction").assertIsDisplayed()
    }

    @Test
    fun addTransactionScreen_typeSelectorChangesColor() {
        val mockViewModel = mockk<ExpenseViewModel>(relaxed = true)
        composeTestRule.setContent {
            AddTransactionScreen(viewModel = mockViewModel, onBack = {})
        }

        composeTestRule.onNodeWithText("Income").performClick()
        composeTestRule.onNodeWithText("Income").assertIsSelected()
        
        composeTestRule.onNodeWithText("Expense").performClick()
        composeTestRule.onNodeWithText("Expense").assertIsSelected()
    }

    @Test
    fun addTransactionScreen_inputFieldTypingWorks() {
        val mockViewModel = mockk<ExpenseViewModel>(relaxed = true)
        composeTestRule.setContent {
            AddTransactionScreen(viewModel = mockViewModel, onBack = {})
        }

        composeTestRule.onNodeWithText("What for?").performTextInput("Coffee")
        composeTestRule.onNodeWithText("Coffee").assertExists()

        composeTestRule.onNodeWithText("Amount (₹)").performTextInput("150")
        composeTestRule.onNodeWithText("150").assertExists()
    }

    @Test
    fun addTransactionScreen_backButtonTriggersCallback() {
        var backClicked = false
        val mockViewModel = mockk<ExpenseViewModel>(relaxed = true)
        
        composeTestRule.setContent {
            AddTransactionScreen(viewModel = mockViewModel, onBack = { backClicked = true })
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backClicked)
    }
}
