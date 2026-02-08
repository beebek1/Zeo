package com.example.zeo.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeo.local.ExpenseEntity
import com.example.zeo.viewmodel.ExpenseViewModel
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(viewModel: ExpenseViewModel) {
    val transactions by viewModel.expenses.collectAsState()
    
    val totalIncome = transactions.filter { it.transactionType == "Income" }.sumOf { it.amount }
    val totalExpense = abs(transactions.filter { it.transactionType == "Expense" }.sumOf { it.amount })
    
    Scaffold(
        containerColor = screenBackground,
        topBar = {
            TopAppBar(
                title = { Text("Analytics", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bluish)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Summary Card
            item {
                AnalyticsSummaryCard(totalIncome, totalExpense)
            }

            // 2. Spending by Category (Donut Chart)
            item {
                val expenseTransactions = transactions.filter { it.transactionType == "Expense" }
                if (expenseTransactions.isNotEmpty()) {
                    CategoryDistributionCard(expenseTransactions)
                }
            }

            // 3. Bar Chart (Income vs Expense)
            item {
                IncomeVsExpenseBarChart(totalIncome, totalExpense)
            }
            
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun AnalyticsSummaryCard(income: Double, expense: Double) {
    val total = income + expense
    val incomeWeight = if (total > 0) (income / total).toFloat() else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Total Volume", color = Color.Gray, fontSize = 14.sp)
            Text("₹%.2f".format(income + expense), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color(0xFFEEEEEE), CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(incomeWeight)
                        .background(Color(0xFF00C853), CircleShape)
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Income", color = Color(0xFF00C853), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Expense", color = Color(0xFFD50000), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CategoryDistributionCard(expenses: List<ExpenseEntity>) {
    val categoryTotals = expenses.groupBy { it.tag }
        .mapValues { abs(it.value.sumOf { exp -> exp.amount }) }
    
    val totalExpense = categoryTotals.values.sum()
    val colors = listOf(bluish, Color(0xFFFF9800), Color(0xFF9C27B0), Color(0xFFE91E63), Color(0xFF4CAF50), Color(0xFF00BCD4))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Expense by Category", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Donut Chart
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(140.dp)) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        var startAngle = -90f
                        categoryTotals.values.forEachIndexed { index, amount ->
                            val sweepAngle = (amount / totalExpense).toFloat() * 360f
                            drawArc(
                                color = colors[index % colors.size],
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = 30f, cap = StrokeCap.Round)
                            )
                            startAngle += sweepAngle
                        }
                    }
                    Text("Expenses", fontSize = 12.sp, color = Color.Gray)
                }

                // Legend
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    categoryTotals.keys.take(4).forEachIndexed { index, category ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(10.dp).background(colors[index % colors.size], CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(category, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IncomeVsExpenseBarChart(income: Double, expense: Double) {
    val maxVal = maxOf(income, expense).toFloat()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Income vs Expense", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(30.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Income Bar
                BarItem(
                    label = "Income",
                    value = income.toFloat(),
                    maxVal = maxVal,
                    color = Color(0xFF00C853)
                )
                
                // Expense Bar
                BarItem(
                    label = "Expense",
                    value = expense.toFloat(),
                    maxVal = maxVal,
                    color = Color(0xFFD50000)
                )
            }
        }
    }
}

@Composable
fun BarItem(label: String, value: Float, maxVal: Float, color: Color) {
    val heightFactor = if (maxVal > 0) value / maxVal else 0f
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("₹%.0f".format(value), fontSize = 10.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .width(40.dp)
                .fillMaxHeight(heightFactor * 0.8f)
                .background(color, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}
