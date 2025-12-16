package com.example.futsal.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.futsal.repository.UserRepoImpl
import com.example.futsal.ui.theme.Black
import com.example.futsal.ui.theme.Blue
import com.example.futsal.ui.theme.Green
import com.example.futsal.ui.theme.Lightgray
import com.example.futsal.R
import com.example.futsal.viewmodel.UserViewModel


class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginBody()
        }
    }
}

@Composable
fun LoginBody(){

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as Activity

    val snackbarHostState = remember { SnackbarHostState() }

    val userViewModel = remember { UserViewModel(UserRepoImpl()) }

    val coroutineScope = rememberCoroutineScope()

    val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)

    val localEmail: String? = sharedPreferences.getString("email","")
    val localPassword: String? = sharedPreferences.getString("password","")


    Scaffold(
        containerColor = Green,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding->
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues = padding)

        ) {
            if(showDialog){
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                    },
                    confirmButton = {
                        Text("Ok")
                    },
                    dismissButton = {
                        Text("Cancel")
                    },
                    title = {
                        Text("Confirm")
                    },
                    text = {
                        Text("Are you sure you want  to delete")
                    },
                    properties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true
                    )
                )
            }

            Column (
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
                ){
                    Image(
                        painter = painterResource(R.drawable.splash),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Welcome Back",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Sign in to continue to Goalpost",
                    color = Color.White,
                    fontSize = 16.sp
                )

            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    // Set a max height to control how much space the form takes
                    .fillMaxHeight(0.65f) // Takes up the bottom 65% of the screen
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(Color.White)
                    .padding(24.dp),
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
                        focusedIndicatorColor = Green,
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
                        Text("**********")
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            visibility = !visibility
                        }) {
                            Icon(
                                painter = if (!visibility)
                                    painterResource(R.drawable.baseline_visibility_off_24)
                                else
                                    painterResource(R.drawable.baseline_visibility_24),
                                contentDescription = null,
                            )
                        }

                    },
                    visualTransformation = if (!visibility) PasswordVisualTransformation() else VisualTransformation.None,

                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Lightgray,
                        focusedContainerColor = Lightgray,
                        focusedIndicatorColor = Green,
                        unfocusedIndicatorColor = Color.Transparent
                    ),

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    shape = RoundedCornerShape(15.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Forget Password?",
                    modifier = Modifier.clickable {
                        val intent = Intent(context, ForgetPassword::class.java)
                        context.startActivity(intent)
                    }
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    style = TextStyle(textAlign = TextAlign.End),
                )

                Button(
                    onClick = {
                        if (email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Enter valid email and password",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            userViewModel.login(email, password) { success, message ->
                                if (success) {
                                    val intent = Intent(context, Dashboard::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)

                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 15.dp),
                    colors = ButtonDefaults.buttonColors(Green)
                ) {
                    Text("Log In", fontSize = 20.sp)
                }



                Text(buildAnnotatedString {
                    append("Don't have account? ")

                    withStyle(SpanStyle(color = Color.Green)) {
                        append("Sign up")
                    }
                }, modifier = Modifier.clickable {
                    val intent = Intent(
                        context,
                        Registration::class.java
                    )
                    context.startActivity(intent)


                }
                )


            }

        }
    }

}




@Preview
@Composable
fun LoginBodyPreview(){
    LoginBody()
}
