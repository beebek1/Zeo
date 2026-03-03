package com.example.zeo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zeo.ZeoApplication
import com.example.zeo.local.ExpenseEntity
import com.example.zeo.repository.UserRepoImpl
import com.example.zeo.viewmodel.ExpenseViewModel
import com.example.zeo.viewmodel.ExpenseViewModelFactory
import com.example.zeo.viewmodel.UserViewModel

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val expenseViewModel: ExpenseViewModel = viewModel(
                factory = ExpenseViewModelFactory((application as ZeoApplication).expenseRepository)
            )
            
            val userViewModel = remember { UserViewModel(UserRepoImpl()) }

            var showAddTransactionScreen by remember { mutableStateOf(false) }
            var selectedExpenseId by remember { mutableStateOf<Int?>(null) }
            
            var expenseToDelete by remember { mutableStateOf<ExpenseEntity?>(null) }

            if (expenseToDelete != null) {
                AlertDialog(
                    onDismissRequest = { expenseToDelete = null },
                    title = { Text("Delete Transaction") },
                    text = { Text("Are you sure you want to delete this transaction?") },
                    confirmButton = {
                        TextButton(onClick = {
                            expenseToDelete?.let { expenseViewModel.deleteExpense(it) }
                            expenseToDelete = null
                        }) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { expenseToDelete = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            if (showAddTransactionScreen) {
                AddTransactionScreen(
                    viewModel = expenseViewModel,
                    expenseId = selectedExpenseId,
                    onBack = { 
                        showAddTransactionScreen = false
                        selectedExpenseId = null
                    })
            } else {
                MainScreen(
                    viewModel = expenseViewModel,
                    userViewModel = userViewModel,
                    onAddTransactionClicked = { 
                        selectedExpenseId = null
                        showAddTransactionScreen = true 
                    },
                    onTransactionClicked = { id ->
                        selectedExpenseId = id
                        showAddTransactionScreen = true
                    },
                    onDeleteTransaction = { expense ->
                        expenseToDelete = expense
                    })
            }
        }
    }
}
