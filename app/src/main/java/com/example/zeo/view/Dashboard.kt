package com.example.zeo.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.zeo.model.Transaction

val bluish = Color(0xFF3F94F8)
val screenBackground = Color(0xFFF4F6F8)

class Dashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        NavHost(navController, startDestination = NavigationItem.Home.route, modifier = Modifier.padding(it)) {
            composable(NavigationItem.Home.route) {
                DashboardScreen(navController)
            }
            composable(NavigationItem.Analytics.route) {
                AnalyticsScreen()
            }
            composable(NavigationItem.Profile.route) {
                ProfileScreen()
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Analytics,
        NavigationItem.Profile
    )
    NavigationBar(
        containerColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                selected = currentRoute == item.route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = bluish,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color.Transparent
                ),
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let {
                            popUpTo(it) { saveState = true }
                        }
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
    object Analytics : NavigationItem("analytics", Icons.Default.Analytics)
    object Profile : NavigationItem("profile", Icons.Default.AccountCircle)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavHostController) {
    val transactions = listOf(
        Transaction("Salary", 40000.0, "Income", "Salary", "1 June 2024", "Monthly Salary", System.currentTimeMillis(), id = "1"),
        Transaction("Netflix", 999.0, "Expense", "Entertainment", "2 June 2024", "Subscription", System.currentTimeMillis(), id = "2"),
        Transaction("Freelance", 5000.0, "Income", "Freelance", "3 June 2024", "Project X", System.currentTimeMillis(), id = "3"),
        Transaction("Dinner", 1500.0, "Expense", "Food", "3 June 2024", "With friends", System.currentTimeMillis(), id = "4")
    )

    Scaffold(
        containerColor = screenBackground,
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { navController.navigate(NavigationItem.Profile.route) }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bluish),
                modifier = Modifier.height(48.dp) // reduced from default ~64dp

            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Handle FAB click */ },
                containerColor = bluish,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                BalanceSummaryCard(transactions)
            }
            item {
                Text(
                    text = "Recent Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(transactions, key = { it.id }) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
fun BalanceSummaryCard(transactions: List<Transaction>) {
    val totalIncome = transactions.filter { it.transactionType == "Income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.transactionType == "Expense" }.sumOf { it.amount }
    val totalBalance = totalIncome - totalExpense

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Total Balance", fontSize = 16.sp, color = Color.Gray)
            Text(text = "₹%.2f".format(totalBalance), fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Income", fontSize = 14.sp, color = Color.Gray)
                    Text(text = "+₹%.2f".format(totalIncome), fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF00C853))
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Expense", fontSize = 14.sp, color = Color.Gray)
                    Text(text = "-₹%.2f".format(totalExpense), fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFD50000))
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = transaction.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = transaction.tag, fontSize = 14.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "${if (transaction.transactionType == "Income") "+" else "-"}₹%.2f".format(transaction.amount),
                    color = if (transaction.transactionType == "Income") Color(0xFF00C853) else Color(0xFFD50000),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(text = transaction.date, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MainScreen()
}
