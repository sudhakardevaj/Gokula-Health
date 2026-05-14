package com.mindmatrix.gokulahealth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.mindmatrix.gokulahealth.domain.repository.CattleRepository
import com.mindmatrix.gokulahealth.ui.navigation.NavGraph
import com.mindmatrix.gokulahealth.ui.theme.GokulaHealthTheme
import com.mindmatrix.gokulahealth.util.MockDataGenerator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Inject the repository to pass to our mock generator
    @Inject
    lateinit var repository: CattleRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // ✅ Inject Mock Data on startup
        lifecycleScope.launch {
            MockDataGenerator.populateDatabase(repository)
        }

        setContent {
            GokulaHealthTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}