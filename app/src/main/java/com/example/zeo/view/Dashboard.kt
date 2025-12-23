package com.example.zeo.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.zeo.local.ExpenseEntity
import com.example.zeo.viewmodel.ExpenseViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(viewModel: ExpenseViewModel, onAddTransactionClicked: () -> Unit) {
    val userId = "test_user" // Hardcoded for now - replace with actual user ID
    LaunchedEffect(userId) {
        viewModel.getExpenses(userId)
    }

    val expenses by viewModel.expenses.collectAsState()
    var selectedFilter by remember { mutableStateOf("Overall") }

    val filteredExpenses = when (selectedFilter) {
        "Income" -> expenses.filter { it.type == "Income" }
        "Expense" -> expenses.filter { it.type == "Expense" }
        else -> expenses
    }

    val totalIncome = expenses.filter { it.type == "Income" }.sumOf { it.amount }
    val totalExpense = expenses.filter { it.type == "Expense" }.sumOf { it.amount }
    val totalBalance = totalIncome + totalExpense // Corrected: Expense amounts are negative

    val listState = rememberLazyListState()
    val isFabVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 || !listState.isScrollInProgress
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(onClick = { onAddTransactionClicked() }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Summary Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryCard("Total Balance", "$%.2f".format(totalBalance), MaterialTheme.colorScheme.primary)
                SummaryCard("Income", "+$%.2f".format(totalIncome), Color(0xFF00897B))
                SummaryCard("Expense", "$%.2f".format(totalExpense), Color(0xFFE53935))
            }

            // Filter Dropdown
            FilterDropdown(selectedFilter) { newFilter ->
                selectedFilter = newFilter
            }


            // Transaction List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                items(filteredExpenses, key = { it.id }) { expense ->
                    val dismissState = rememberSwipeToDismissBoxState()

                    LaunchedEffect(dismissState.currentValue) {
                        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                            scope.launch {
                                viewModel.deleteExpense(expense)
                                val result = snackbarHostState.showSnackbar(
                                    message = "Transaction deleted",
                                    actionLabel = "Undo"
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.addExpense(expense) // Re-add the expense to undo
                                }
                            }
                        }
                    }

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color = when (dismissState.dismissDirection) {
                                SwipeToDismissBoxValue.EndToStart -> Color.Red
                                else -> Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .background(color),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        },
                        content = { TransactionItem(expense = expense) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(selectedFilter: String, onFilterSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedFilter,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Overall") },
                    onClick = {
                        onFilterSelected("Overall")
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Income") },
                    onClick = {
                        onFilterSelected("Income")
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Expense") },
                    onClick = {
                        onFilterSelected("Expense")
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun SummaryCard(title: String, amount: String, color: Color) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .wrapContentWidth(),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = Color.White)
            Text(text = amount, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun TransactionItem(expense: ExpenseEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = expense.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = expense.category, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = if (expense.type == "Income") "+$%.2f".format(expense.amount) else "$%.2f".format(expense.amount),
                color = if (expense.type == "Income") Color(0xFF00897B) else Color(0xFFE53935),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}