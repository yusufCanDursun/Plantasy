package com.example.plantasy.pages

import android.app.Application
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantasy.model.FirebaseManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalendarViewModel : ViewModel() {
    private val _completedDays = MutableStateFlow<List<Int>>(emptyList())
    val completedDays: StateFlow<List<Int>> = _completedDays.asStateFlow()

    private lateinit var firebaseManager: FirebaseManager

    fun initialize(context: Context) {
        firebaseManager = FirebaseManager(context)
    }

    fun loadCompletedDays(userId: String) {
        viewModelScope.launch {
            if (::firebaseManager.isInitialized) {
                val days = firebaseManager.getCompletedDays(userId)
                _completedDays.value = days
            }
        }
    }

    fun addCompletedDay(userId: String, day: Int) {
        viewModelScope.launch {
            if (::firebaseManager.isInitialized) {
                val isSuccess = firebaseManager.addCompletedDay(userId, day)
                if (isSuccess) {
                    _completedDays.value = _completedDays.value + day
                }
            }
        }
    }

    fun removeCompletedDay(userId: String, day: Int) {
        viewModelScope.launch {
            if (::firebaseManager.isInitialized) {
                val isSuccess = firebaseManager.removeCompletedDay(userId, day)
                if (isSuccess) {
                    _completedDays.value = _completedDays.value - day
                }
            }
        }
    }
}

@Composable
fun CalendarPage(viewModel: CalendarViewModel = viewModel()) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val completedDays by viewModel.completedDays.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.initialize(context)
        userId?.let { uid ->
            viewModel.loadCompletedDays(uid)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Takvim",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CalendarGrid(
            daysInMonth = 30,
            completedDays = completedDays,
            onDayClick = { day ->
                userId?.let { uid ->
                    if (completedDays.contains(day)) {
                        viewModel.removeCompletedDay(uid, day)
                    } else {
                        viewModel.addCompletedDay(uid, day)
                    }
                }
            }
        )
    }
}

@Composable
fun CalendarGrid(
    daysInMonth: Int,
    completedDays: List<Int>,
    onDayClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Pt", "Sa", "Ça", "Pe", "Cu", "Ct", "Pa").forEach { day ->
                Text(
                    text = day,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        for (week in 0 until daysInMonth / 7 + 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in 1..7) {
                    val currentDay = week * 7 + day
                    if (currentDay <= daysInMonth) {
                        CalendarDay(
                            day = currentDay,
                            isCompleted = completedDays.contains(currentDay),
                            onClick = { onDayClick(currentDay) }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarDay(
    day: Int,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(4.dp)
            .background(
                color = if (isCompleted) Color(0xFF4CAF50) else Color.LightGray,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}