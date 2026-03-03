package com.example.zeo.view

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeo.local.ExpenseEntity
import com.example.zeo.viewmodel.ExpenseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: ExpenseViewModel, 
    expenseId: Int? = null, // null for new, Int for update
    onBack: () -> Unit
) {
    val transactions by viewModel.expenses.collectAsState()
    val existingExpense = remember(expenseId) { transactions.find { it.id == expenseId } }

    var title by remember { mutableStateOf(existingExpense?.title ?: "") }
    var amount by remember { mutableStateOf(existingExpense?.amount?.let { kotlin.math.abs(it).toString() } ?: "") }
    var transactionType by remember { mutableStateOf(existingExpense?.transactionType ?: "Expense") }
    
    val expenseCategories = listOf("Food", "Rent", "Travel", "Entertainment", "Shopping", "Health", "Other")
    val incomeCategories = listOf("Salary", "Business", "Freelance", "Investment", "Gift", "Other")
    
    var category by remember { mutableStateOf(existingExpense?.tag ?: if (transactionType == "Income") incomeCategories[0] else expenseCategories[0]) }
    var note by remember { mutableStateOf(existingExpense?.note ?: "") }
    
    val context = LocalContext.current
    var categoryExpanded by remember { mutableStateOf(false) }

    // Dynamic colors for distinction
    val incomeColor = Color(0xFF4CAF50)
    val expenseColor = Color(0xFFFF5252)
    val themeColor by animateColorAsState(if (transactionType == "Income") incomeColor else expenseColor, label = "color")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (expenseId == null) "Add Transaction" else "Update Transaction", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = themeColor)
            )
        },
        containerColor = screenBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Type Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf("Expense", "Income").forEach { type ->
                    val isSelected = transactionType == type
                    Button(
                        onClick = { 
                            transactionType = type
                            category = if (type == "Income") incomeCategories[0] else expenseCategories[0]
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) themeColor else Color.Transparent,
                            contentColor = if (isSelected) Color.White else Color.Gray
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = null
                    ) {
                        Text(type, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("What for?") },
                placeholder = { Text("e.g. Rent or Salary") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = themeColor, focusedLabelColor = themeColor)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
                label = { Text("Amount (₹)") },
                placeholder = { Text("0.00") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = themeColor, focusedLabelColor = themeColor)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category Selector
            val currentCategories = if (transactionType == "Income") incomeCategories else expenseCategories
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = themeColor, focusedLabelColor = themeColor)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    currentCategories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = themeColor, focusedLabelColor = themeColor)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull()
                    if (title.isBlank() || amountDouble == null || amountDouble <= 0) {
                        Toast.makeText(context, "Please enter valid title and amount", Toast.LENGTH_SHORT).show()
                    } else {
                        val expense = ExpenseEntity(
                            id = expenseId ?: 0,
                            userId = "default_user", 
                            title = title,
                            amount = if (transactionType == "Expense") -amountDouble else amountDouble,
                            transactionType = transactionType,
                            tag = category,
                            date = existingExpense?.date ?: System.currentTimeMillis(),
                            note = note
                        )
                        if (expenseId == null) viewModel.addExpense(expense) else viewModel.updateExpense(expense)
                        onBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = themeColor)
            ) {
                Text(if (expenseId == null) "Save Transaction" else "Update Transaction", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
