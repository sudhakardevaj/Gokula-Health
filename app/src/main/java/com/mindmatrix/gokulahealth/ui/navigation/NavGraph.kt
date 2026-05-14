package com.mindmatrix.gokulahealth.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mindmatrix.gokulahealth.ui.screen.AddCattleScreen
import com.mindmatrix.gokulahealth.ui.screen.AddVaccinationScreen
import com.mindmatrix.gokulahealth.ui.screen.BreedingScreen
import com.mindmatrix.gokulahealth.ui.screen.CattleDetailScreen
import com.mindmatrix.gokulahealth.ui.screen.GenAISuggestionScreen
import com.mindmatrix.gokulahealth.ui.screen.GlobalSearchScreen
import com.mindmatrix.gokulahealth.ui.screen.HealthNotesScreen
import com.mindmatrix.gokulahealth.ui.screen.LogMilkScreen
import com.mindmatrix.gokulahealth.ui.screen.MainScreen
import com.mindmatrix.gokulahealth.ui.screen.SemenLogScreen
import com.mindmatrix.gokulahealth.ui.screen.VaccinationScreen
import com.mindmatrix.gokulahealth.ui.screen.YieldChartScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable("main") {
            MainScreen(
                onAddCattle = { navController.navigate("add_cattle") },
                onCattleClick = { id -> navController.navigate("cattle_detail/$id") },
                onSearchClick = { navController.navigate("global_search") }
            )
        }

        // ✅ FIXED: Now accepts optional cattleId for EDIT mode!
        // Was always opening empty form before!
        composable(
            route = "add_cattle?cattleId={cattleId}",
            arguments = listOf(
                navArgument("cattleId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val cattleId = backStackEntry.arguments?.getInt("cattleId") ?: -1
            AddCattleScreen(
                onNavigateBack = { navController.popBackStack() },
                cattleId = cattleId // ✅ FIXED: Pass cattleId for edit!
            )
        }

        composable(
            route = "cattle_detail/{cattleId}",
            arguments = listOf(
                navArgument("cattleId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val cattleId = backStackEntry.arguments?.getInt("cattleId") ?: -1
            CattleDetailScreen(
                cattleId = cattleId,
                onLogMilk = {
                    navController.navigate("log_milk/$cattleId")
                },
                onViewYieldChart = {
                    navController.navigate("yield_chart/$cattleId")
                },
                onViewVaccinations = {
                    navController.navigate("vaccination/$cattleId")
                },
                onViewHealthNotes = { cattleName ->
                    // ✅ FIXED: Pass BOTH cattleId and cattleName!
                    navController.navigate("health_notes/$cattleId/$cattleName")
                },
                onEditProfile = {
                    // ✅ FIXED: Navigate with cattleId to pre-fill edit form!
                    navController.navigate("add_cattle?cattleId=$cattleId")
                },
                onNavigateToBreeding = {
                    navController.navigate("breeding/$cattleId")
                },
                onNavigateToSemenLog = { // ✅ NEW
                    navController.navigate("semen_log/$cattleId")
                },
                onNavigateToGenAI = {
                    navController.navigate("genai_suggestion/$cattleId")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ✅ FIXED: Now accepts BOTH cattleId AND cattleName!
        // Before it only had cattleName but HealthNotesScreen needs cattleId for DB!
        composable(
            route = "health_notes/{cattleId}/{cattleName}",
            arguments = listOf(
                navArgument("cattleId") { type = NavType.IntType },
                navArgument("cattleName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val cattleId = backStackEntry.arguments?.getInt("cattleId") ?: -1
            val cattleName = backStackEntry.arguments?.getString("cattleName") ?: ""
            HealthNotesScreen(
                cattleId = cattleId,     // ✅ NEW: for DB queries
                cattleName = cattleName, // ✅ for display
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "log_milk/{cattleId}",
            arguments = listOf(
                navArgument("cattleId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val cattleId = backStackEntry.arguments?.getInt("cattleId") ?: -1
            LogMilkScreen(
                cattleId = cattleId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "yield_chart/{cattleId}",
            arguments = listOf(
                navArgument("cattleId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val cattleId = backStackEntry.arguments?.getInt("cattleId") ?: -1
            YieldChartScreen(
                cattleId = cattleId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "vaccination/{cattleId}",
            arguments = listOf(
                navArgument("cattleId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val cattleId = backStackEntry.arguments?.getInt("cattleId") ?: -1
            VaccinationScreen(
                cattleId = cattleId,
                onAddVaccination = {
                    navController.navigate("add_vaccination/$cattleId")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "add_vaccination/{cattleId}",
            arguments = listOf(
                navArgument("cattleId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val cattleId = backStackEntry.arguments?.getInt("cattleId") ?: -1
            AddVaccinationScreen(
                cattleId = cattleId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "genai_suggestion/{cattleId}",
            arguments = listOf(
                navArgument("cattleId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val cattleId = backStackEntry.arguments?.getInt("cattleId") ?: -1
            GenAISuggestionScreen(
                cattleId = cattleId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "breeding/{cattleId}",
            arguments = listOf(
                navArgument("cattleId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val cattleId = backStackEntry.arguments?.getInt("cattleId") ?: -1
            BreedingScreen(
                cattleId = cattleId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "semen_log/{cattleId}",
            arguments = listOf(
                navArgument("cattleId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val cattleId = backStackEntry.arguments?.getInt("cattleId") ?: -1
            SemenLogScreen(
                cattleId = cattleId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("global_search") {
            GlobalSearchScreen(
                onCattleClick = { id ->
                    navController.navigate("cattle_detail/$id")
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
