package com.example.zeo.view

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeo.viewmodel.ExpenseViewModel
import com.example.zeo.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(userViewModel: UserViewModel, expenseViewModel: ExpenseViewModel) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    
    val currentUser = userViewModel.getCurrentUser()
    val userId = currentUser?.uid ?: ""
    
    // Fetch user data when screen opens
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            userViewModel.getUserById(userId)
        }
    }
    
    val userModel by userViewModel.users.observeAsState()
    
    var showEditDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = screenBackground,
        topBar = {
            TopAppBar(
                title = { Text("Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bluish)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(20.dp)
        ) {
            // Profile Header with Dynamic Data
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(bluish.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = bluish
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = userModel?.fullName ?: "Loading...",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = userModel?.email ?: currentUser?.email ?: "No Email",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }

            // Account Settings Section
            item {
                ProfileSectionTitle("Account Settings")
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Edit,
                    title = "Edit Profile",
                    subtitle = "Update your display name",
                    onClick = { showEditDialog = true }
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Configure app alerts",
                    onClick = { showNotificationDialog = true }
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.DeleteForever,
                    title = "Delete Account",
                    subtitle = "Permanently remove your data",
                    iconColor = Color(0xFFD50000),
                    onClick = { showDeleteAccountDialog = true }
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            // App Settings Section
            item {
                ProfileSectionTitle("App Settings")
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Help,
                    title = "Help & Support",
                    subtitle = "Contact us or view FAQs",
                    onClick = { Toast.makeText(context, "Redirecting to Support...", Toast.LENGTH_SHORT).show() }
                )
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }

            // Logout Button
            item {
                Button(
                    onClick = {
                        userViewModel.logout(userId) {
                            with(sharedPreferences.edit()) {
                                putBoolean("remember_me", false)
                                remove("email")
                                remove("password")
                                apply()
                            }
                            val intent = Intent(context, Login::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFEBEE))
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color(0xFFD50000))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", color = Color(0xFFD50000), fontWeight = FontWeight.Bold)
                }
            }
            
            item { Spacer(modifier = Modifier.height(50.dp)) }
        }
    }

    // Dynamic Edit Profile Dialog
    if (showEditDialog) {
        var newName by remember { mutableStateOf(userModel?.fullName ?: "") }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {
                    Text("Update your full name", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = bluish,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && userModel != null) {
                            val updatedUser = userModel!!.copy(fullName = newName)
                            userViewModel.updateProfile(userId, updatedUser) { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    userViewModel.getUserById(userId)
                                    showEditDialog = false
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = bluish)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Dynamic Notification Dialog
    if (showNotificationDialog) {
        var isEnabled by remember { mutableStateOf(true) }
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = { Text("Notifications") },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Push Notifications")
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = bluish)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationDialog = false }) {
                    Text("Done", color = bluish)
                }
            }
        )
    }

    // Delete Account Confirmation Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account") },
            text = { Text("Are you sure you want to delete your account? This will permanently remove all your data, including transactions.") },
            confirmButton = {
                Button(
                    onClick = {
                        expenseViewModel.deleteAllExpensesForUser(userId)
                        userViewModel.deleteUser(userId) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) {
                                with(sharedPreferences.edit()) {
                                    putBoolean("remember_me", false)
                                    remove("email")
                                    remove("password")
                                    apply()
                                }
                                val intent = Intent(context, Login::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            }
                        }
                        showDeleteAccountDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD50000))
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = bluish,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector, 
    title: String, 
    subtitle: String, 
    iconColor: Color = Color.Gray,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(screenBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = iconColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
