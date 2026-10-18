package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RomanticRoseColorScheme = lightColorScheme(
  primary = RosePrimary,
  onPrimary = Color.White,
  primaryContainer = RoseTertiary,
  onPrimaryContainer = RoseTextPrimary,
  secondary = RoseSecondary,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFFFD6E0),
  tertiary = HeartGlowPink,
  background = RoseBackground,
  onBackground = RoseTextPrimary,
  surface = RoseSurface,
  onSurface = RoseTextPrimary,
  surfaceVariant = Color(0xFFFFEBF0),
  onSurfaceVariant = RoseTextSecondary
)

private val LavenderDreamColorScheme = lightColorScheme(
  primary = LavenderPrimary,
  onPrimary = Color.White,
  primaryContainer = LavenderTertiary,
  onPrimaryContainer = Color(0xFF371650),
  secondary = LavenderSecondary,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFF0E4FB),
  tertiary = RoseSecondary,
  background = LavenderBackground,
  onBackground = Color(0xFF2C133E),
  surface = LavenderSurface,
  onSurface = Color(0xFF2C133E),
  surfaceVariant = Color(0xFFEFE8F7),
  onSurfaceVariant = Color(0xFF6E4D84)
)

private val GoldenChampagneColorScheme = lightColorScheme(
  primary = GoldPrimary,
  onPrimary = Color.White,
  primaryContainer = GoldTertiary,
  onPrimaryContainer = Color(0xFF483307),
  secondary = GoldSecondary,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFFBF4E2),
  tertiary = RosePrimary,
  background = GoldBackground,
  onBackground = Color(0xFF382909),
  surface = GoldSurface,
  onSurface = Color(0xFF382909),
  surfaceVariant = Color(0xFFF6EEDB),
  onSurfaceVariant = Color(0xFF7D6026)
)

private val SweetMidnightColorScheme = darkColorScheme(
  primary = MidnightPrimary,
  onPrimary = Color.White,
  primaryContainer = Color(0xFF5B1A3B),
  onPrimaryContainer = Color(0xFFFFD6E7),
  secondary = MidnightSecondary,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF382256),
  tertiary = GoldSparkle,
  background = MidnightBackground,
  onBackground = MidnightTextPrimary,
  surface = MidnightSurface,
  onSurface = MidnightTextPrimary,
  surfaceVariant = MidnightCard,
  onSurfaceVariant = Color(0xFFCEB8D9)
)

@Composable
fun LoveLoomTheme(
  themeIndex: Int = 0,
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = when (themeIndex) {
    0 -> if (darkTheme) SweetMidnightColorScheme else RomanticRoseColorScheme
    1 -> LavenderDreamColorScheme
    2 -> GoldenChampagneColorScheme
    3 -> SweetMidnightColorScheme
    else -> RomanticRoseColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
