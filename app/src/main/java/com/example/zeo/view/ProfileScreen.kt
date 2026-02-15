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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zeo.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(userViewModel: UserViewModel) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
    val userEmail = sharedPreferences.getString("email", "User@example.com") ?: "User@example.com"

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
            // Profile Header
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
                        text = userEmail.split("@")[0].replaceFirstChar { it.uppercase() },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = userEmail,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }

            // Settings Sections
            item {
                ProfileSectionTitle("Account Settings")
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Edit,
                    title = "Edit Profile",
                    subtitle = "Change your name or email",
                    onClick = { Toast.makeText(context, "Edit Profile clicked", Toast.LENGTH_SHORT).show() }
                )
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Manage your alert settings",
                    onClick = { Toast.makeText(context, "Notifications clicked", Toast.LENGTH_SHORT).show() }
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            item {
                ProfileSectionTitle("App Settings")
            }

            item {
                ProfileMenuItem(
                    icon = Icons.Default.Help,
                    title = "Help & Support",
                    subtitle = "Contact us or view FAQs",
                    onClick = { Toast.makeText(context, "Help & Support clicked", Toast.LENGTH_SHORT).show() }
                )
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }

            // Logout Button
            item {
                Button(
                    onClick = {
                        // Logout logic
                        with(sharedPreferences.edit()) {
                            putBoolean("remember_me", false)
                            remove("email")
                            remove("password")
                            apply()
                        }
                        val intent = Intent(context, Login::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
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


//skjfhkfk
//skjfhkfk
//skjfhkfk//skjfhkfk
//skjfhkfk
//skjfhkfk
//skjfhkfk
//skjfhkfk
//skjfhkfk//skjfhkfkv



@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
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
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray)
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
