package io.github.mamedovilkin.weather.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.mamedovilkin.weather.ui.activity.WeatherAppActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// IMPORTANT: To run this test comment this line of code: launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) in HomeScreen.kt
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<WeatherAppActivity>()

    @Test
    fun testOnNavigateToSearch() {
        composeRule.waitUntil(5000) {
            composeRule
                .onNodeWithText("Wind")
                .isDisplayed()
        }

        composeRule
            .onNodeWithTag("navigate_to_search")
            .performClick()

        composeRule
            .onNodeWithText("Globe")
            .assertIsDisplayed()
    }
}