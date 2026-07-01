package com.lucasneves.financecontrol.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary                 = Teal40,
    onPrimary               = Color.White,
    primaryContainer        = Teal90,
    onPrimaryContainer      = Teal10,
    secondary               = Blue40,
    onSecondary             = Color.White,
    secondaryContainer      = Blue90,
    onSecondaryContainer    = Blue10,
    tertiary                = Blue30,
    onTertiary              = Color.White,
    tertiaryContainer       = Blue95,
    onTertiaryContainer     = Blue10,
    background              = Teal99,
    onBackground            = Neutral10,
    surface                 = Teal99,
    onSurface               = Neutral10,
    surfaceVariant          = Neutral90,
    onSurfaceVariant        = Neutral40,
    outline                 = Neutral40,
    error                   = Color(0xFFBA1A1A),
    onError                 = Color.White,
    errorContainer          = Color(0xFFFFDAD6),
    onErrorContainer        = Color(0xFF410002),
    surfaceContainerLowest  = Color.White,
    surfaceContainerLow     = Neutral95,
    surfaceContainer        = Neutral90,
    surfaceContainerHigh    = Color(0xFFD0DCDE),
    surfaceContainerHighest = Neutral80,
)

private val DarkColorScheme = darkColorScheme(
    primary                 = Teal80,
    onPrimary               = Teal20,
    primaryContainer        = Teal30,
    onPrimaryContainer      = Teal90,
    secondary               = Blue80,
    onSecondary             = Blue20,
    secondaryContainer      = Blue30,
    onSecondaryContainer    = Blue90,
    tertiary                = Blue80,
    onTertiary              = Blue20,
    tertiaryContainer       = Blue30,
    onTertiaryContainer     = Blue90,
    background              = Teal10,
    onBackground            = Neutral95,
    surface                 = Teal10,
    onSurface               = Neutral95,
    surfaceVariant          = Neutral30,
    onSurfaceVariant        = Neutral80,
    outline                 = Neutral40,
    error                   = Color(0xFFFFB4AB),
    onError                 = Color(0xFF690005),
    errorContainer          = Color(0xFF93000A),
    onErrorContainer        = Color(0xFFFFDAD6),
    surfaceContainerLowest  = Teal10,
    surfaceContainerLow     = Neutral20,
    surfaceContainer        = Neutral30,
    surfaceContainerHigh    = Color(0xFF3B4244),
    surfaceContainerHighest = Color(0xFF454D4F),
)

@Composable
fun FinanceControlTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
