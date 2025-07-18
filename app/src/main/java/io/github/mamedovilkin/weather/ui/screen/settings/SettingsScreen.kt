package io.github.mamedovilkin.weather.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.mamedovilkin.weather.R
import io.github.mamedovilkin.weather.ui.theme.primary
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import io.github.mamedovilkin.weather.network.model.TemperatureUnit
import io.github.mamedovilkin.weather.ui.theme.onPrimary

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        settingsViewModel.fetchUnit()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.settings),
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.temperature_unit),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = primary,
                modifier = Modifier.weight(1F)
            )
            Row(
                modifier = Modifier
                    .clickable { settingsViewModel.setExpanded(true) }
                    .testTag("temperature_unit")
            ) {
                Text(
                    text = if (uiState.temperatureUnit == TemperatureUnit.IMPERIAL) {
                        stringResource(R.string.fahrenheit)
                    } else {
                        stringResource(R.string.celsius)
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primary
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = primary
                )
                DropdownMenu(
                    expanded = uiState.expanded,
                    onDismissRequest = { settingsViewModel.setExpanded(false) }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.celsius)) },
                        onClick = {
                            settingsViewModel.setExpanded(false)
                            settingsViewModel.setUnit(TemperatureUnit.METRIC)
                        },
                        modifier = Modifier.testTag("celsius")
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.fahrenheit)) },
                        onClick = {
                            settingsViewModel.setExpanded(false)
                            settingsViewModel.setUnit(TemperatureUnit.IMPERIAL)
                        },
                        modifier = Modifier.testTag("fahrenheit")
                    )
                }
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = onPrimary,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .alpha(0.5F)
        )
    }
}