package com.example.plantasy.pages

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.plantasy.ui.theme.BGGreen
import com.example.plantasy.ui.theme.ButtonBG
import com.example.plantasy.model.FirebaseManager
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.example.plantasy.model.AddPlant

@Composable
fun LogInPage(navController: NavController) {
    LogInMain(navController)
}

@Composable
fun LogInMain(navController: NavController) {
    var emailState = remember { mutableStateOf("") }
    var passwordState = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }

    val firebaseManager = FirebaseManager(context = LocalContext.current)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp, 50.dp, 25.dp, 50.dp)
                .background(
                    BGGreen,
                    shape = RoundedCornerShape(42.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp , 50.dp , 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextLogIn()
                Spacer(modifier = Modifier.height(28.dp))
                EmailInput(emailState)
                Spacer(modifier = Modifier.height(20.dp))
                PasswordInput(passwordState)
                Spacer(modifier = Modifier.height(28.dp))
                if (errorMessage.value.isNotEmpty()) {
                    Text(text = errorMessage.value, color = Color.Red)
                }
                LogInButton(
                    email = emailState.value,
                    password = passwordState.value,
                    navController,
                    errorMessage
                )
                Spacer(modifier = Modifier.height(12.dp))
                SignUpButton(navController)
            }
        }
    }
}

@Composable
fun EmailInput(emailState: MutableState<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        TextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

@Composable
fun PasswordInput(passwordState: MutableState<String>) {
    TextField(
        value = passwordState.value,
        onValueChange = { passwordState.value = it },
        label = { Text("Password") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        visualTransformation = PasswordVisualTransformation()
    )
}

@Composable
fun TextLogIn() {
    Text(
        text = "Log In",
        color = Color.White,
        style = androidx.compose.ui.text.TextStyle(
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun LogInButton(
    email: String,
    password: String,
    navController: NavController,
    errorMessage: MutableState<String>
) {
    val firebaseManager = FirebaseManager(context = LocalContext.current)

    Button(
        onClick = {
            if (email.isBlank() || password.isBlank()) {
                errorMessage.value = "Email veya şifre boş olamaz"
                return@Button
            }
            firebaseManager.loginUser(
                email = email,
                password = password,
                onSuccess = {
                    navController.navigate("home")
                },

            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ButtonBG)
    ) {
        Text("Log In", color = Color.White)
    }
}

@Composable
fun SignUpButton(navController: NavController) {
    Button(
        onClick = {
            navController.navigate("register")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonBG
        )
    ) {
        Text("Sign Up", color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LogInPage(navController = rememberNavController())
}
