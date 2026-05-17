package io.github.mamedovilkin.weather.ui.screen.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.mamedovilkin.weather.BuildConfig
import io.github.mamedovilkin.weather.R
import io.github.mamedovilkin.weather.domain.model.PressureUnit
import io.github.mamedovilkin.weather.domain.model.TemperatureUnit
import io.github.mamedovilkin.weather.domain.model.WindSpeedUnit
import io.github.mamedovilkin.weather.ui.theme.onPrimary
import io.github.mamedovilkin.weather.ui.theme.primary
import org.koin.androidx.compose.koinViewModel
import java.util.*

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val message = stringResource(R.string.restart_the_app_to_apply_changes)

    LaunchedEffect(Unit) {
        settingsViewModel.fetchUnits()
    }

    LaunchedEffect(uiState.showMassage) {
        if (uiState.showMassage) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            settingsViewModel.setShowMassage(false)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
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
        TemperatureUnitItem(
            uiState = uiState,
            onExpand = { settingsViewModel.setExpandedTemperatureUnit(it) },
            onItemSelected = {
                settingsViewModel.setUnit(it)
                settingsViewModel.setExpandedTemperatureUnit(false)
                settingsViewModel.setShowMassage(true)
            }
        )
        SettingsDivider()
        WindSpeedUnitItem(
            uiState = uiState,
            onExpand = { settingsViewModel.setExpandedWindSpeedUnit(it) },
            onItemSelected = {
                settingsViewModel.setUnit(it)
                settingsViewModel.setExpandedWindSpeedUnit(false)
                settingsViewModel.setShowMassage(true)
            }
        )
        SettingsDivider()
        PressureUnitItem(
            uiState = uiState,
            onExpand = { settingsViewModel.setExpandedPressureUnit(it) },
            onItemSelected = {
                settingsViewModel.setUnit(it)
                settingsViewModel.setExpandedPressureUnit(false)
                settingsViewModel.setShowMassage(true)
            }
        )
        SettingsDivider()
        LanguageItem(context)
        SettingsDivider()
        FeedbackItem(context)
        SettingsDivider()
        AppVersionItem(context)
        Spacer(modifier = Modifier.height(72.dp))
    }
}

@Composable
fun TemperatureUnitItem(
    uiState: SettingsUiState,
    onExpand: (Boolean) -> Unit,
    onItemSelected: (TemperatureUnit) -> Unit
) {
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
                .clickable { onExpand(true) }
        ) {
            Text(
                text = if (uiState.temperatureUnit == TemperatureUnit.FAHRENHEIT) {
                    stringResource(R.string.fahrenheit)
                } else {
                    stringResource(R.string.celsius)
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = primary
            )
            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = null,
                tint = primary
            )
            DropdownMenu(
                expanded = uiState.expandedTemperatureUnit,
                onDismissRequest = { onExpand(false) }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.celsius)) },
                    onClick = {
                        onItemSelected(TemperatureUnit.CELSIUS)
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.fahrenheit)) },
                    onClick = {
                        onItemSelected(TemperatureUnit.FAHRENHEIT)
                    }
                )
            }
        }
    }
}

@Composable
fun WindSpeedUnitItem(
    uiState: SettingsUiState,
    onExpand: (Boolean) -> Unit,
    onItemSelected: (WindSpeedUnit) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.wind_speed_unit),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = primary,
            modifier = Modifier.weight(1F)
        )
        Row(
            modifier = Modifier
                .clickable { onExpand(true) }
        ) {
            Text(
                text = when (uiState.windSpeedUnit) {
                    WindSpeedUnit.KMH -> {
                        stringResource(R.string.kmh_unit)
                    }
                    WindSpeedUnit.MPH -> {
                        stringResource(R.string.mph_unit)
                    }
                    else -> {
                        stringResource(R.string.ms_unit)
                    }
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = primary
            )
            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = null,
                tint = primary
            )
            DropdownMenu(
                expanded = uiState.expandedWindSpeedUnit,
                onDismissRequest = { onExpand(false) }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.kmh_unit)) },
                    onClick = {
                        onItemSelected(WindSpeedUnit.KMH)
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.ms_unit)) },
                    onClick = {
                        onItemSelected(WindSpeedUnit.MS)
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.mph_unit)) },
                    onClick = {
                        onItemSelected(WindSpeedUnit.MPH)
                    }
                )
            }
        }
    }
}

@Composable
fun PressureUnitItem(
    uiState: SettingsUiState,
    onExpand: (Boolean) -> Unit,
    onItemSelected: (PressureUnit) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.pressure_unit),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = primary,
            modifier = Modifier.weight(1F)
        )
        Row(
            modifier = Modifier
                .clickable { onExpand(true) }
        ) {
            Text(
                text = if (uiState.pressureUnit == PressureUnit.MB) {
                    stringResource(R.string.mb_unit)
                } else {
                    stringResource(R.string.mmhg_unit)
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = primary
            )
            Icon(
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = null,
                tint = primary
            )
            DropdownMenu(
                expanded = uiState.expandedPressureUnit,
                onDismissRequest = { onExpand(false) }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.mb_unit)) },
                    onClick = {
                        onItemSelected(PressureUnit.MB)
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.mmhg_unit)) },
                    onClick = {
                        onItemSelected(PressureUnit.MMHG)
                    }
                )
            }
        }
    }
}

@SuppressLint("LocalContextConfigurationRead")
@Composable
fun LanguageItem(context: Context) {
    val locale = context.resources.configuration.locales[0]
    val language = locale.getDisplayLanguage(locale)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .clickable {
                context.startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            .padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.language),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = primary,
            modifier = Modifier.weight(1F)
        )
        Row {
            Text(
                text = language.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = primary
            )
            Icon(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = null,
                tint = primary,
                modifier = Modifier
                    .padding(4.dp)
                    .size(14.dp)
            )
        }
    }
}

@Composable
fun FeedbackItem(
    context: Context
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .clickable {
                try {
                    val subject = context.getString(R.string.app_name)
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:${BuildConfig.FEEDBACK_EMAIL}?subject=$subject".toUri()
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, context.getString(R.string.please_install_an_email_app), Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
            .padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.feedback),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = primary,
            modifier = Modifier.weight(1F)
        )
        Icon(
            painter = painterResource(R.drawable.ic_arrow_right),
            contentDescription = null,
            tint = primary,
            modifier = Modifier
                .padding(4.dp)
                .size(14.dp)
        )
    }
}

@Composable
fun AppVersionItem(
    context: Context
) {
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = stringResource(R.string.app_version),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = primary,
            modifier = Modifier.weight(1F)
        )
        Text(
            text = "${packageInfo.versionName} (${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) packageInfo.longVersionCode else @Suppress("DEPRECATION") packageInfo.versionCode.toLong()})",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = primary
        )
    }
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = onPrimary,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .alpha(0.5F)
    )
}