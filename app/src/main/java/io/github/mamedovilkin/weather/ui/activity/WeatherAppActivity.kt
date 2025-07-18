@file:OptIn(InternalSerializationApi::class)

package io.github.mamedovilkin.weather.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import io.github.mamedovilkin.weather.ui.common.Screen
import io.github.mamedovilkin.weather.ui.common.WeatherBottomBar
import io.github.mamedovilkin.weather.ui.screen.home.HomeScreen
import io.github.mamedovilkin.weather.ui.screen.search.SearchScreen
import io.github.mamedovilkin.weather.ui.screen.settings.SettingsScreen
import io.github.mamedovilkin.weather.ui.theme.WeatherTheme
import io.github.mamedovilkin.weather.ui.theme.background
import androidx.compose.runtime.getValue
import kotlinx.serialization.InternalSerializationApi

@AndroidEntryPoint
class WeatherAppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherTheme {
                WeatherApp()
            }
        }
    }
}

@Composable
fun WeatherApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        color = background,
        modifier = Modifier.fillMaxSize()
    ) {
        Box {
            NavHost(
                navController = navController,
                graph = navController
                    .createGraph(startDestination = Screen.Home.route) {
                        composable(route = Screen.Home.route) {
                            HomeScreen { city ->
                                navController.navigate("search_screen/$city")
                            }
                        }
                        composable(
                            route = Screen.Search.route,
                            arguments = listOf(navArgument("city") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val city = backStackEntry.arguments?.getString("city")
                            SearchScreen(
                                city = city,
                                onNavigateToHome = { navController.navigate(Screen.Home.route) }
                            )
                        }
                        composable(route = Screen.Settings.route) {
                            SettingsScreen()
                        }
                    },
                modifier = Modifier.navigationBarsPadding()
            )
            WeatherBottomBar(
                currentRoute,
                onNavigate = { navController.navigate(it) },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}