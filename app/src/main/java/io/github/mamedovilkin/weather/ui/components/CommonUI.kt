package io.github.mamedovilkin.weather.ui.components

import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewOutlineProvider
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.my.target.common.CachePolicy
import com.my.target.common.models.IAdLoadingError
import com.my.target.nativeads.NativeAd
import com.my.target.nativeads.banners.NativePromoBanner
import com.my.target.nativeads.views.NativeAdView
import io.github.mamedovilkin.weather.R
import io.github.mamedovilkin.weather.ui.theme.background
import io.github.mamedovilkin.weather.ui.theme.cardBackgroundGradientEnd
import io.github.mamedovilkin.weather.ui.theme.navigation
import io.github.mamedovilkin.weather.ui.theme.onPrimary
import io.github.mamedovilkin.weather.ui.theme.primary
import io.github.mamedovilkin.weather.ui.theme.surface

sealed class Screen(val route: String) {
    object Home: Screen("home_screen")
    object Search: Screen("search_screen/{city}")
    object Settings: Screen("settings_screen")
    object Locations: Screen("locations_screen")
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
                tint = if (currentRoute == Screen.Home.route || currentRoute == Screen.Locations.route) primary else onPrimary,
                modifier = Modifier
                    .padding(24.dp)
                    .size(24.dp)
                    .clickable {
                        onNavigate(Screen.Home.route)
                    }
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
            )
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    setSearchQuery: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClose: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = searchQuery,
        onValueChange = { setSearchQuery(it) },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions (
            onSearch = {
                onSearch(searchQuery)
                keyboardController?.hide()
            }
        ),
        singleLine = true,
        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = null,
                tint = background
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = null,
                    tint = background,
                    modifier = Modifier.clickable { onClose() }
                )
            }
        },
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        textStyle = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = primary,
            unfocusedContainerColor = primary,
            disabledContainerColor = primary,
            focusedTextColor = background,
            unfocusedTextColor = background,
            cursorColor = background,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(36.dp),
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(8.dp)
    )
}

@Composable
fun AdBanner(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var ad by remember { mutableStateOf<NativeAd?>(null) }

    LaunchedEffect(Unit) {
        val nativeAd = NativeAd(1917863, context)

        nativeAd.listener = object : NativeAd.NativeAdListener {
            override fun onLoad(promo: NativePromoBanner, loadedAd: NativeAd) {
                ad = loadedAd
            }

            override fun onNoAd(error: IAdLoadingError, nativeAd: NativeAd) {}
            override fun onClick(p0: View?, p1: NativeAd) {}
            override fun onClick(nativeAd: NativeAd) {}
            override fun onVideoPlay(nativeAd: NativeAd) {}
            override fun onVideoPause(nativeAd: NativeAd) {}
            override fun onVideoComplete(nativeAd: NativeAd) {}
            override fun onShow(nativeAd: NativeAd) {}
        }

        nativeAd.cachePolicy = CachePolicy.NONE
        nativeAd.load()
    }

    AnimatedVisibility(ad != null) {
        ad?.banner?.let { banner ->
            AndroidView(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                factory = { context ->
                    NativeAdView(context).apply {
                        setBackgroundColor(surface.toArgb())
                        setPadding(
                            ((8.dp).value * context.resources.displayMetrics.density).toInt(),
                            ((8.dp).value * context.resources.displayMetrics.density).toInt(),
                            ((8.dp).value * context.resources.displayMetrics.density).toInt(),
                            ((8.dp).value * context.resources.displayMetrics.density).toInt(),
                        )
                        ageRestrictionTextView.background = GradientDrawable().apply {
                            shape = GradientDrawable.RECTANGLE
                            cornerRadius = ((2.dp).value * context.resources.displayMetrics.density)
                            setStroke(((1.dp).value * context.resources.displayMetrics.density).toInt(), Color.Gray.toArgb())
                        }
                        iconView.clipToOutline = true
                        iconView.outlineProvider = object : ViewOutlineProvider() {
                            override fun getOutline(view: View, outline: Outline) {
                                outline.setRoundRect(0, 0, view.width, view.height, ((8.dp).value * context.resources.displayMetrics.density))
                            }
                        }
                        mediaAdView?.clipToOutline = true
                        mediaAdView?.outlineProvider = object : ViewOutlineProvider() {
                            override fun getOutline(view: View, outline: Outline) {
                                outline.setRoundRect(0, 0, view.width, view.height, ((16.dp).value * context.resources.displayMetrics.density))
                            }
                        }
                        titleTextView.setTextColor(Color.White.toArgb())
                        descriptionTextView.setTextColor(Color.White.toArgb())
                        ctaButtonView.setTextColor(Color.White.toArgb())
                        ctaButtonView.setBackgroundColor(cardBackgroundGradientEnd.toArgb())
                        ctaButtonView.elevation = 0F
                        ctaButtonView.clipToOutline = true
                        ctaButtonView.outlineProvider = object : ViewOutlineProvider() {
                            override fun getOutline(view: View, outline: Outline) {
                                outline.setRoundRect(0, 0, view.width, view.height, (16 * context.resources.displayMetrics.density))
                            }
                        }
                    }
                },
                update = { nativeAdView ->
                    nativeAdView.setupView(banner)
                    ad?.registerView(nativeAdView)
                }
            )
        }
    }
}