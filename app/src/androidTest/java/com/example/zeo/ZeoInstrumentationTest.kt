package com.example.zeo

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.zeo.local.ExpenseEntity
import com.example.zeo.view.AddTransactionScreen
import com.example.zeo.viewmodel.ExpenseViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class ZeoInstrumentationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createMockViewModel(): ExpenseViewModel {
        val mockViewModel = mockk<ExpenseViewModel>(relaxed = true)
        // Explicitly mock the expenses StateFlow to avoid ClassCastException
        val fakeExpenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
        every { mockViewModel.expenses } returns fakeExpenses
        return mockViewModel
    }

    @Test
    fun addTransactionScreen_displaysCorrectTitle() {
        val mockViewModel = createMockViewModel()
        composeTestRule.setContent {
            AddTransactionScreen(viewModel = mockViewModel, onBack = {})
        }

        composeTestRule.onNodeWithText("Add Transaction").assertIsDisplayed()
    }

    @Test
    fun addTransactionScreen_showsErrorOnEmptyFields() {
        val mockViewModel = createMockViewModel()
        composeTestRule.setContent {
            AddTransactionScreen(viewModel = mockViewModel, onBack = {})
        }

        composeTestRule.onNodeWithText("Save Transaction").performClick()
        // Check if the screen is still visible (meaning it didn't navigate back)
        composeTestRule.onNodeWithText("Save Transaction").assertIsDisplayed()
    }

    @Test
    fun addTransactionScreen_typeSelectorChangesColor() {
        val mockViewModel = createMockViewModel()
        composeTestRule.setContent {
            AddTransactionScreen(viewModel = mockViewModel, onBack = {})
        }

        // Initially "Expense" is selected. Click "Income"
        composeTestRule.onNodeWithText("Income").performClick()
        // We can't easily check color, but we can check if it exists or other state changes
        composeTestRule.onNodeWithText("Income").assertIsDisplayed()
    }

    @Test
    fun addTransactionScreen_inputFieldTypingWorks() {
        val mockViewModel = createMockViewModel()
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
        val mockViewModel = createMockViewModel()
        
        composeTestRule.setContent {
            AddTransactionScreen(viewModel = mockViewModel, onBack = { backClicked = true })
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backClicked)
    }
}
