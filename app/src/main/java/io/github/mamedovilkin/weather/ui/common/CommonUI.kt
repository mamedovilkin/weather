package io.github.mamedovilkin.weather.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.mamedovilkin.weather.R
import io.github.mamedovilkin.weather.ui.theme.navigation
import io.github.mamedovilkin.weather.ui.theme.onPrimary
import io.github.mamedovilkin.weather.ui.theme.primary

sealed class Screen(val route: String) {
    object Home: Screen("home_screen")
    object Search: Screen("search_screen/{city}")
    object Settings: Screen("settings_screen")
}

@Composable
fun WeatherBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
        colors = CardDefaults.cardColors(
            containerColor = navigation
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_sun),
                contentDescription = null,
                tint = if (currentRoute == Screen.Home.route) primary else onPrimary,
                modifier = Modifier
                    .padding(24.dp)
                    .size(24.dp)
                    .clickable {
                        onNavigate(Screen.Home.route)
                    }
                    .testTag("home")
            )
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = if (currentRoute == Screen.Search.route) primary else onPrimary,
                modifier = Modifier
                    .padding(24.dp)
                    .size(24.dp)
                    .clickable {
                        onNavigate(Screen.Search.route)
                    }
                    .testTag("search")
            )
            Icon(
                painter = painterResource(R.drawable.ic_settings),
                contentDescription = null,
                tint = if (currentRoute == Screen.Settings.route) primary else onPrimary,
                modifier = Modifier
                    .padding(24.dp)
                    .size(24.dp)
                    .clickable {
                        onNavigate(Screen.Settings.route)
                    }
                    .testTag("settings")
            )
        }
    }
}
