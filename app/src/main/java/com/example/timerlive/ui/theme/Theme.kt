package com.example.timerlive.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = GlowPink,
    onPrimary = GlowDeep,
    secondary = GlowBlue,
    onSecondary = GlowSoft,
    tertiary = GlowMint,
    background = GlowSoft,
    onBackground = GlowDeep,
    surface = GlowSoft,
    onSurface = GlowDeep
)

private val DarkColors = darkColorScheme(
    primary = GlowPink,
    onPrimary = GlowSoft,
    secondary = GlowBlue,
    onSecondary = GlowSoft,
    tertiary = GlowMint,
    background = GlowDeep,
    onBackground = GlowSoft,
    surface = GlowDeep,
    onSurface = GlowSoft
)

@Composable
fun TimerLiveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = colorScheme.background.toArgb()
                it.navigationBarColor = colorScheme.background.toArgb()
                val controller = WindowCompat.getInsetsController(it, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
