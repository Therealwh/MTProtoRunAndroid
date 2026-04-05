package com.mtprorun.presentation.navigation

sealed class Screens(val route: String) {
    object Home : Screens("home")
    object Settings : Screens("settings")
}
