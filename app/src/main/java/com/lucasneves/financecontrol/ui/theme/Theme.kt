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
    primary                = Gold40,
    onPrimary              = Color.White,
    primaryContainer       = Gold90,
    onPrimaryContainer     = Gold10,
    secondary              = Silver40,
    onSecondary            = Color.White,
    secondaryContainer     = Silver90,
    onSecondaryContainer   = Silver10,
    tertiary               = Green40,
    onTertiary             = Color.White,
    tertiaryContainer      = Green90,
    onTertiaryContainer    = Green10,
    background             = Gold99,
    onBackground           = Gold10,
    surface                = Gold99,
    onSurface              = Gold10,
    surfaceVariant         = Silver90,
    onSurfaceVariant       = Silver30,
    outline                = Silver40,
    error                  = Color(0xFFBA1A1A),
    onError                = Color.White,
    errorContainer         = Color(0xFFFFDAD6),
    onErrorContainer       = Color(0xFF410002),
)

private val DarkColorScheme = darkColorScheme(
    primary                = Gold80,
    onPrimary              = Gold20,
    primaryContainer       = Gold30,
    onPrimaryContainer     = Gold90,
    secondary              = Silver80,
    onSecondary            = Silver20,
    secondaryContainer     = Silver30,
    onSecondaryContainer   = Silver90,
    tertiary               = Green80,
    onTertiary             = Green20,
    tertiaryContainer      = Green30,
    onTertiaryContainer    = Green90,
    background             = Gold10,
    onBackground           = Gold95,
    surface                = Gold10,
    onSurface              = Gold95,
    surfaceVariant         = Silver30,
    onSurfaceVariant       = Silver80,
    outline                = Silver40,
    error                  = Color(0xFFFFB4AB),
    onError                = Color(0xFF690005),
    errorContainer         = Color(0xFF93000A),
    onErrorContainer       = Color(0xFFFFDAD6),
)

@Composable
fun FinanceControlTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color desabilitado — garante nosso tema ouro/prata/verde em qualquer Android
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
