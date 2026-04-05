package com.mtprorun.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mtprorun.R
import com.mtprorun.presentation.screens.HomeScreen
import com.mtprorun.presentation.screens.SettingsScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = com.mtprorun.presentation.theme.CardGradientStart) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.home)) },
                    label = { Text(stringResource(R.string.home)) },
                    selected = currentRoute == Screens.Home.route,
                    onClick = { navController.navigate(Screens.Home.route) { launchSingleTop = true } }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings)) },
                    label = { Text(stringResource(R.string.settings)) },
                    selected = currentRoute == Screens.Settings.route,
                    onClick = { navController.navigate(Screens.Settings.route) { launchSingleTop = true } }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screens.Home.route) { HomeScreen() }
            composable(Screens.Settings.route) { SettingsScreen() }
        }
    }
}
