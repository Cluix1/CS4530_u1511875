package com.example.navigationdemov2

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MyAppNav (myNavController: NavHostController, startDestination: String){
    NavHost(myNavController, startDestination){
        composable ("home") { HomeScreen(myNavController)  }
        composable ("profile/{username}") {
            val username = it.arguments?.getString("username")
            ProfileScreen(myNavController, username) }

    }
}