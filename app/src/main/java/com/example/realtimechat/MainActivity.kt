package com.example.realtimechat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.realtimechat.presentation.navigation.ChatNavGraph
import com.example.realtimechat.presentation.navigation.Screen
import com.example.realtimechat.ui.theme.RealTimeChatTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RealTimeChatTheme {
                val navController = rememberNavController()
                val currentUser = FirebaseAuth.getInstance().currentUser
                val currentUserId = currentUser?.uid.orEmpty()
                val startDestination = if (currentUser != null) Screen.Home.route else Screen.Auth.route

                ChatNavGraph(
                    navController = navController,
                    currentUserId = currentUserId,
                    startDestination = startDestination
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RealTimeChatTheme {
        Greeting("Android")
    }
}