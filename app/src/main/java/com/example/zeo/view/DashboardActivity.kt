package com.example.zeo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zeo.ZeoApplication
import com.example.zeo.viewmodel.ExpenseViewModel
import com.example.zeo.viewmodel.ExpenseViewModelFactory

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val expenseViewModel: ExpenseViewModel = viewModel(
                factory = ExpenseViewModelFactory((application as ZeoApplication).expenseRepository)
            )

            var showAddTransactionScreen by remember { mutableStateOf(false) }

            if (showAddTransactionScreen) {
                AddTransactionScreen(
                    viewModel = expenseViewModel,
                    onTransactionAdded = { showAddTransactionScreen = false })
            } else {
                Dashboard(
                    viewModel = expenseViewModel,
                    onAddTransactionClicked = { showAddTransactionScreen = true })
            }
        }
    }
}