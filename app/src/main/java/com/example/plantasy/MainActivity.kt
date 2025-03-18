package com.example.plantasy

import HomePage
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plantasy.model.AddPlant
import com.example.plantasy.pages.CalendarPage
import com.example.plantasy.pages.LogInPage
import com.example.plantasy.pages.RegisterPage
import com.example.plantasy.ui.theme.PlantasyTheme
import com.example.plantasy.pages.PlantInfo
import com.example.plantasy.pages.PlantListPage
import com.example.plantasy.pages.ProfilePage


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantasyTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LogInPage(navController)
                    }
                    composable("register") {
                        RegisterPage(navController)
                    }
                }
            }
        }
    }
}
