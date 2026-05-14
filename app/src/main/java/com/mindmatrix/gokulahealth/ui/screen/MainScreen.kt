package com.mindmatrix.gokulahealth.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mindmatrix.gokulahealth.ui.component.GokulaIcons
import com.mindmatrix.gokulahealth.ui.theme.LightMeadow
import com.mindmatrix.gokulahealth.ui.theme.MeadowGreen

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home_tab", "Home", Icons.Default.Home)
    object MilkLog : Screen("milk_log_tab", "Milk Log", Icons.Default.LocalDrink)
    object Cattle : Screen("cattle_tab", "Cattle", GokulaIcons.Cow)
    object Vaccination : Screen("vaccination_tab", "Vaccination", Icons.Default.Vaccines)
}

@Composable
fun MainScreen(
    onAddCattle: () -> Unit,
    onCattleClick: (Int) -> Unit,
    onSearchClick: () -> Unit
) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Home,
        Screen.MilkLog,
        Screen.Cattle,
        Screen.Vaccination
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = MeadowGreen
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == screen.route
                        } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MeadowGreen,
                            selectedTextColor = MeadowGreen,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = LightMeadow
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ✅ PRESERVED - was working correctly
            composable(Screen.Home.route) {
                HomeScreen(
                    onAddCattle = onAddCattle,
                    onCattleClick = onCattleClick,
                    onSearchClick = onSearchClick
                )
            }

            // ✅ FIXED: Was showing YieldChartScreen(cattleId=1) which is WRONG!
            // Now shows AllMilkLogsScreen - a summary of all cattle milk logs
            composable(Screen.MilkLog.route) {
                AllMilkLogsScreen(
                    onCattleClick = onCattleClick
                )
            }

            // ✅ PRESERVED - was working correctly
            composable(Screen.Cattle.route) {
                CattleListScreen(
                    onCattleClick = onCattleClick,
                    onAddCattle = onAddCattle,
                    onSearchClick = onSearchClick // ✅ NEW
                )
            }

            // ✅ FIXED: Was passing cattleId = -1 which showed nothing!
            // Now shows AllVaccinationsScreen - vaccination list for ALL cattle
            composable(Screen.Vaccination.route) {
                AllVaccinationsScreen(
                    onNavigateToAddCattle = onAddCattle
                )
            }
        }
    }
}