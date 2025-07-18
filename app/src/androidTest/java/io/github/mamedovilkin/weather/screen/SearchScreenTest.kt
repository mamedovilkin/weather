package io.github.mamedovilkin.weather.screen

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.mamedovilkin.weather.ui.activity.WeatherAppActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

// IMPORTANT: To run this test comment this line of code: launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) in HomeScreen.kt
@RunWith(AndroidJUnit4::class)
class SearchScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<WeatherAppActivity>()

    @Test
    fun testOnNavigateToHome() {
        composeRule
            .onNodeWithTag("search")
            .performClick()

        composeRule
            .onNode(hasTestTag("card") and hasText("New York,\nUSA"))
            .performClick()

        composeRule.waitUntil(10000) {
            composeRule
                .onNodeWithText("New York")
                .isDisplayed()
        }
    }

    @Test
    fun testNoResultsFound() {
        composeRule
            .onNodeWithTag("search")
            .performClick()

        composeRule
            .onNodeWithTag("search_bar")
            .performTextInput("testtesttest")

        composeRule
            .onNodeWithTag("search_bar")
            .performImeAction()

        composeRule.waitUntil(5000) {
            composeRule
                .onNodeWithText("No results for testtesttest")
                .isDisplayed()
        }
    }

    @Test
    fun testSearchLocation() {
        composeRule
            .onNodeWithTag("search")
            .performClick()

        composeRule
            .onNodeWithTag("search_bar")
            .performTextInput("Moscow")

        composeRule
            .onNodeWithTag("search_bar")
            .performImeAction()

        composeRule.waitUntil(5000) {
            composeRule
                .onNodeWithText("Moscow")
                .isDisplayed()
        }
    }

    @Test
    fun testSetLocation() {
        composeRule
            .onNodeWithTag("search")
            .performClick()

        composeRule
            .onNodeWithTag("search_bar")
            .performTextInput("Moscow")

        composeRule
            .onNodeWithTag("search_bar")
            .performImeAction()

        composeRule.waitUntil(5000) {
            composeRule
                .onNodeWithText("Moscow, RU")
                .isDisplayed()
        }

        composeRule
            .onAllNodesWithText("Set location")
            .onFirst()
            .performClick()

        composeRule
            .onNodeWithTag("home")
            .performClick()

        composeRule.waitUntil(5000) {
            composeRule
                .onNodeWithText("Moscow")
                .isDisplayed()
        }
    }
}