package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = Color(0xFF00E5FF),
  secondary = Color(0xFF9146FF),
  tertiary = Color(0xFFE5A00D),
  background = Color.Transparent,
  surface = Color.Transparent,
  onPrimary = Color.Black,
  onSecondary = Color.White,
  onBackground = Color.White,
  onSurface = Color.White
)

@Composable
fun LumiaTheme(content: @Composable () -> Unit) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
