package com.example.plantasy.pages

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.plantasy.model.User
import com.example.plantasy.ui.theme.BGGreen
import com.example.plantasy.ui.theme.ButtonBG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.plantasy.model.FirebaseManager
import androidx.compose.ui.platform.LocalContext


@Composable
fun RegisterPage(navController: NavController) {
    RegisterMain(navController)
}

@Composable
fun RegisterMain(navController: NavController) {
    var usernameState = remember { mutableStateOf(TextFieldValue("")) }
    var emailState = remember { mutableStateOf(TextFieldValue("")) }
    var passwordState = remember { mutableStateOf(TextFieldValue("")) }
    var passwordAgainState = remember { mutableStateOf(TextFieldValue("")) }

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
                    .padding(20.dp, 50.dp, 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextSignUp()
                Spacer(modifier = Modifier.height(28.dp))
                UserNameInput(usernameState)
                Spacer(modifier = Modifier.height(12.dp))
                RegisterEmailInput(emailState)
                Spacer(modifier = Modifier.height(12.dp))
                RegisterPasswordInput(passwordState)
                Spacer(modifier = Modifier.height(12.dp))
                PasswordAgainInput(passwordAgainState)
                Spacer(modifier = Modifier.height(28.dp))
                SignUpButton(
                    username = usernameState.value.text,
                    email = emailState.value.text,
                    password = passwordState.value.text ,
                    passwordAgain = passwordAgainState.value.text ,

                )
                Spacer(modifier = Modifier.height(12.dp))
                LogInButton(navController)
            }
        }
    }
}

@Composable
fun TextSignUp() {
    Text(
        text = "Sign Up",
        color = Color.White,
        style = androidx.compose.ui.text.TextStyle(
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
        )
    )
}

@Composable
fun UserNameInput(usernameState: MutableState<TextFieldValue>) {
    TextField(
        value = usernameState.value,
        onValueChange = { usernameState.value = it },
        label = { Text("Username") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
fun RegisterEmailInput(emailState: MutableState<TextFieldValue>) {
    TextField(
        value = emailState.value,
        onValueChange = { emailState.value = it },
        label = { Text("Email") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    )
}

@Composable
fun RegisterPasswordInput(passwordState: MutableState<TextFieldValue>) {
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
fun PasswordAgainInput(passwordAgainState: MutableState<TextFieldValue>) {
    TextField(
        value = passwordAgainState.value,
        onValueChange = { passwordAgainState.value = it },
        label = { Text("Password Again") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        visualTransformation = PasswordVisualTransformation()
    )
}

@Composable
fun SignUpButton(
    username: String,
    email: String,
    password: String,
    passwordAgain: String,
) {
    val context = LocalContext.current
    val firebaseManager = FirebaseManager(context)
    val errorMessage = remember { mutableStateOf("") }

    Button(
        onClick = {
            if (password != passwordAgain) {
                errorMessage.value = "Passwords do not match!"
                return@Button
            }

            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                errorMessage.value = "All fields are required!"
                return@Button
            }

            val user = User(
                username = username,
                email = email,
                password = password
            )

            firebaseManager.registerUser(
                user = user,
                onSuccess = {
                    errorMessage.value = "Succesful"

                },
                onFailure = { error ->
                    errorMessage.value = "error"
                }
            )
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

    if (errorMessage.value.isNotEmpty()) {
        Text(
            text = errorMessage.value,
            color = Color.Red,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun LogInButton(navController: NavController) {
    Button(
        onClick = {
            navController.navigate("login")
            println("Log In sayfasına yönlendiriliyor...")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonBG
        )
    ) {
        Text("Log In", color = Color.White)
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterPagePreview() {

}
