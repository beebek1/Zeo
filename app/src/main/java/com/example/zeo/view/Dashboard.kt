package com.example.zeo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.zeo.local.ExpenseEntity
import com.example.zeo.viewmodel.ExpenseViewModel
import com.example.zeo.viewmodel.UserViewModel
import androidx.compose.runtime.livedata.observeAsState

val bluish = Color(0xFF3F94F8)
val screenBackground = Color(0xFFF8FAFC)
val gradientColor = Brush.verticalGradient(listOf(Color(0xFF64B5F6), Color(0xFF1E88E5)))

class Dashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Text("Please launch via DashboardActivity")
        }
    }
}

@Composable
fun MainScreen(
    viewModel: ExpenseViewModel, 
    userViewModel: UserViewModel, 
    onAddTransactionClicked: () -> Unit,
    onTransactionClicked: (Int) -> Unit,
    onDeleteTransaction: (ExpenseEntity) -> Unit // New delete callback
) {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        NavHost(
            navController, 
            startDestination = NavigationItem.Home.route, 
            modifier = Modifier.padding(bottom = padding.calculateBottomPadding())
        ) {
            composable(NavigationItem.Home.route) {
                DashboardScreen(navController, viewModel, userViewModel, onAddTransactionClicked, onTransactionClicked, onDeleteTransaction)
            }
            composable(NavigationItem.Analytics.route) {
                AnalyticsScreen(viewModel)
            }
            composable(NavigationItem.Profile.route) {
                ProfileScreen(userViewModel, viewModel)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(NavigationItem.Home, NavigationItem.Analytics, NavigationItem.Profile)
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                selected = currentRoute == item.route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = bluish,
                    indicatorColor = bluish.copy(alpha = 0.1f)
                ),
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { popUpTo(it) { saveState = true } }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class NavigationItem(var route: String, var icon: ImageVector) {
    object Home : NavigationItem("home", Icons.Default.Home)
    object Analytics : NavigationItem("analytics", Icons.Default.BarChart)
    object Profile : NavigationItem("profile", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController, 
    viewModel: ExpenseViewModel, 
    userViewModel: UserViewModel, 
    onAddTransactionClicked: () -> Unit,
    onTransactionClicked: (Int) -> Unit,
    onDeleteTransaction: (ExpenseEntity) -> Unit
) {
    val transactions by viewModel.expenses.collectAsState()
    val userModel by userViewModel.users.observeAsState()

    Scaffold(
        containerColor = screenBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Hello,", fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
                        Text(userModel?.fullName ?: "User", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(NavigationItem.Profile.route) }) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bluish)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransactionClicked, containerColor = bluish, contentColor = Color.White, shape = CircleShape) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { BalanceSummaryCard(transactions) }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Recent Transactions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    TextButton(onClick = { /* TODO: Navigate to All Transactions screen */ }) { 
                        Text("See All", color = bluish) 
                    }
                }
            }
            items(transactions, key = { it.id }) { transaction -> 
                TransactionItem(
                    transaction, 
                    onClick = { onTransactionClicked(transaction.id) },
                    onDelete = { onDeleteTransaction(transaction) }
                ) 
            }
        }
    }
}

@Composable
fun BalanceSummaryCard(transactions: List<ExpenseEntity>) {
    val totalIncome = transactions.filter { it.transactionType == "Income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.transactionType == "Expense" }.sumOf { it.amount }
    val totalBalance = totalIncome + totalExpense

    Card(
        modifier = Modifier.fillMaxWidth().height(180.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(gradientColor).padding(24.dp)) {
            Column {
                Text("Total Balance", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                Text("₹%.2f".format(totalBalance), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    BalanceInfoItem("Income", "+₹%.2f".format(totalIncome), Icons.Default.ArrowDownward, Color(0xFF4CAF50))
                    BalanceInfoItem("Expense", "₹%.2f".format(totalExpense), Icons.Default.ArrowUpward, Color(0xFFFF5252))
                }
            }
        }
    }
}

@Composable
fun BalanceInfoItem(label: String, amount: String, icon: ImageVector, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
            Text(amount, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TransactionItem(transaction: ExpenseEntity, onClick: () -> Unit, onDelete: () -> Unit) {
    val icon = when (transaction.tag) {
        "Food" -> Icons.Default.Restaurant
        "Rent" -> Icons.Default.Home
        "Salary" -> Icons.Default.Payments
        "Shopping" -> Icons.Default.ShoppingBag
        "Health" -> Icons.Default.MedicalServices
        else -> Icons.Default.Category
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(bluish.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = bluish, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(transaction.tag, fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹%.2f".format(transaction.amount),
                    color = if (transaction.transactionType == "Income") Color(0xFF4CAF50) else Color(0xFFFF5252),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(android.text.format.DateFormat.format("dd MMM", transaction.date).toString(), fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
