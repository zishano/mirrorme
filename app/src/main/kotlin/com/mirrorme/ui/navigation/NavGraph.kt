package com.mirrorme.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mirrorme.ui.home.HomeScreen
import com.mirrorme.ui.persona.PersonaScreen
import com.mirrorme.ui.report.ReportScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Persona : Screen("persona")
    object Report : Screen("report")
}

@Composable
fun MirrorMeNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onViewPersona = { navController.navigate(Screen.Persona.route) },
                onViewReport = { navController.navigate(Screen.Report.route) }
            )
        }
        composable(Screen.Persona.route) {
            PersonaScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Report.route) {
            ReportScreen(onBack = { navController.popBackStack() })
        }
    }
}
