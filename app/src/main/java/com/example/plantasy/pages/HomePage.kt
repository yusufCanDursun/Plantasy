import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.plantasy.Notification.NotificationWorker
import com.example.plantasy.ui.theme.BGGreen
import com.example.plantasy.ui.theme.ButtonBG
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.TimeUnit

@Composable
fun HomePage(navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        scheduleNotifications(context)
    }

    HomeMain(navController)
}

@Composable
fun HomeMain(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp)
                .background(
                    BGGreen,
                    shape = RoundedCornerShape(42.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(18.dp))
                TextHomePage()
                Spacer(modifier = Modifier.height(28.dp))
                ButtonGrid(navController)
            }
        }
    }
}

@Composable
fun ButtonGrid(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(0.dp, 26.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            SquareButton(
                label = "Profile",
                onClick = { navController.navigate("profile") },
                modifier = Modifier.weight(1f)
            )
            SquareButton(
                label = "Plants",
                onClick = { navController.navigate("plantlist") },
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            SquareButton(
                label = "Calendar",
                onClick = { navController.navigate("calendar") },
                modifier = Modifier.weight(1f)
            )
            SquareButton(
                label = "Log Out",
                onClick = {
                    val auth = FirebaseAuth.getInstance()
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun SquareButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(color = ButtonBG, shape = RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(label, color = Color.White)
        }
    }
}

@Composable
fun TextHomePage() {
    Text(
        text = "Home Page",
        color = Color.White,
        style = androidx.compose.ui.text.TextStyle(
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )
    )
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomePage(navController = rememberNavController())
}
