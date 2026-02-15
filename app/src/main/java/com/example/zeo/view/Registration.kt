package com.example.zeo.view

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.zeo.R
import androidx.compose.ui.unit.sp
import com.example.zeo.model.UserModel
import com.example.zeo.viewmodel.UserViewModel
import com.example.zeo.repository.UserRepoImpl
import com.example.zeo.ui.theme.Blue
import com.example.zeo.ui.theme.Green
import com.example.zeo.ui.theme.Lightgray
import com.example.zeo.ui.theme.Mint
import com.example.zeo.ui.theme.bluish

class Registration : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegistrationBody()
        }
    }
}

@Composable
fun RegistrationBody() {
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var terms by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    Scaffold(
        containerColor = bluish
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.coinsbro),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Welcome to Zeo",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Let's hit a fresh start",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(Color.White)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { data ->
                        email = data
                    },
                    placeholder = {
                        Text("Email")
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Lightgray,
                        focusedContainerColor = Lightgray,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { data ->
                        fullName = data
                    },
                    placeholder = {
                        Text("Full Name")
                    },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Lightgray,
                        focusedContainerColor = Lightgray,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { data ->
                        password = data
                    },
                    placeholder = {
                        Text("Password")
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            passwordVisibility = !passwordVisibility
                        }) {
                            Icon(
                                painter = if (!passwordVisibility)
                                    painterResource(R.drawable.baseline_visibility_off_24)
                                else
                                    painterResource(R.drawable.baseline_visibility_24),
                                contentDescription = null,
                            )
                        }
                    },
                    visualTransformation = if (!passwordVisibility) PasswordVisualTransformation() else VisualTransformation.None,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Lightgray,
                        focusedContainerColor = Lightgray,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { data ->
                        confirmPassword = data
                    },
                    placeholder = {
                        Text("Confirm Password")
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            confirmPasswordVisibility = !confirmPasswordVisibility
                        }) {
                            Icon(
                                painter = if (!confirmPasswordVisibility)
                                    painterResource(R.drawable.baseline_visibility_off_24)
                                else
                                    painterResource(R.drawable.baseline_visibility_24),
                                contentDescription = null,
                            )
                        }
                    },
                    visualTransformation = if (!confirmPasswordVisibility) PasswordVisualTransformation() else VisualTransformation.None,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Lightgray,
                        focusedContainerColor = Lightgray,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = terms,
                        onCheckedChange = {
                            terms = it
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Blue,
                            checkmarkColor = Mint
                        )
                    )
                    Text("I agree to terms & Conditions")
                }

                Button(
                    onClick = {
                        if (!terms) {
                            Toast.makeText(
                                context,
                                "Please agree terms & conditions",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (password != confirmPassword) {
                            Toast.makeText(
                                context,
                                "Passwords do not match",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            userViewModel.register(email, password) { success, message, userId ->
                                if (success) {
                                    val model = UserModel(
                                        userId = userId,
                                        fullName = fullName,
                                        email = email
                                    )
                                    userViewModel.addUserToDatabase(userId, model) { success, message ->
                                        if (success) {
                                            Toast.makeText(
                                                context,
                                                message,
                                                Toast.LENGTH_LONG
                                            ).show()
                                            val intent = Intent(context, Login::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            context.startActivity(intent)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                message,
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 15.dp),
                    colors = ButtonDefaults.buttonColors(
                        bluish
                    )
                ) {
                    Text(
                        "Sign Up",
                        fontSize = 20.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Don't have account?")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Sign In",
                        color = Color.Blue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            val intent = Intent(
                                context,
                                Login::class.java
                            )
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun RegistrationBodyPreview(){
    RegistrationBody()
}
