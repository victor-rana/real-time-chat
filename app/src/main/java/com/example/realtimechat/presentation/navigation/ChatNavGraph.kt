package com.example.realtimechat.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.realtimechat.presentation.ui.AuthScreen
import com.example.realtimechat.presentation.ui.ChatScreen
import com.example.realtimechat.presentation.ui.chatlist.ChatListScreen
import com.example.realtimechat.presentation.ui.home.HomeScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ChatNavGraph(
    navController: NavHostController,
    currentUserId: String,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                userId = FirebaseAuth.getInstance().currentUser?.displayName ?: "Unknown",
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onStartChat = {
                    navController.navigate(Screen.ChatList.route)
                }
            )
        }

        composable(Screen.ChatList.route) {
            ChatListScreen(
                onChatClick = { selectedUser ->
                    selectedUser.uid.let { uid ->
                        navController.navigate("${Screen.Chat.route}/$uid")
                    }
                }
            )
        }

        composable(Screen.Chat.route + "/{receiverId}") { backStackEntry ->
            val receiverId = backStackEntry.arguments?.getString("receiverId") ?: return@composable
            ChatScreen(
                currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                otherUserId = receiverId
            )
        }
    }

}

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Home : Screen("home")
    object ChatList : Screen("chat_list")
    object Chat : Screen("chat")
}
