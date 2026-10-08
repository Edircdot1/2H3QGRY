package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  primary = PositivusDark,
  onPrimary = Color.White,
  primaryContainer = PositivusGreen,
  onPrimaryContainer = PositivusDark,
  secondary = PositivusSub,
  onSecondary = Color.White,
  tertiary = PositivusGreen,
  onTertiary = PositivusDark,
  background = PositivusCanvas,
  onBackground = PositivusDark,
  surface = Color.White,
  onSurface = PositivusDark,
  surfaceVariant = PositivusGray,
  onSurfaceVariant = PositivusDark,
  outline = PositivusBorder,
  error = AlertRed,
  onError = Color.White
)

private val DarkColorScheme = lightColorScheme(
  primary = PositivusGreen,
  onPrimary = PositivusDark,
  background = PositivusDark,
  onBackground = Color.White,
  surface = PositivusSub,
  onSurface = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
