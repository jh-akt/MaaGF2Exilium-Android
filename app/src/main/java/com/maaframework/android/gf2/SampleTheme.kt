package com.maaframework.android.gf2

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

object MaaGf2DesignTokens {
    object Spacing {
        val xs = 4.dp
        val sm = 8.dp
        val md = 12.dp
        val lg = 16.dp
        val xl = 20.dp
        val xxl = 24.dp
    }

    object CornerRadius {
        val card = 16.dp
        val inner = 12.dp
        val pill = 999.dp
    }
}

private val LightColors = lightColorScheme(
    primary = Color(0xFF2B6BCA),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE5F1FF),
    onPrimaryContainer = Color(0xFF002453),
    secondary = Color(0xFF7B8794),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8EDF3),
    onSecondaryContainer = Color(0xFF1C1F24),
    background = Color(0xFFF5F2ED),
    onBackground = Color(0xFF1C1B18),
    surface = Color(0xFFF9F7F3),
    onSurface = Color(0xFF1C1B18),
    surfaceVariant = Color(0xFFE8E4DE),
    onSurfaceVariant = Color(0xFF6E675F),
    outline = Color(0xFFC9C4BE),
    outlineVariant = Color(0xFFE8E4DE),
    error = Color(0xFFD93025),
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF3E82E8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF15498E),
    onPrimaryContainer = Color(0xFFDCE9FF),
    secondary = Color(0xFF98A2B3),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF252C35),
    onSecondaryContainer = Color(0xFFD8DEE8),
    background = Color(0xFF111214),
    onBackground = Color(0xFFF4F6F8),
    surface = Color(0xFF191B1F),
    onSurface = Color(0xFFF4F6F8),
    surfaceVariant = Color(0xFF242830),
    onSurfaceVariant = Color(0xFF9CA6B4),
    outline = Color(0xFF353B45),
    outlineVariant = Color(0xFF242830),
    error = Color(0xFFFF6B5C),
    onError = Color(0xFF3A0904),
)

private val MaaGf2Shapes = Shapes(
    extraSmall = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.inner),
    small = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.inner),
    medium = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.card),
    large = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.card),
    extraLarge = RoundedCornerShape(MaaGf2DesignTokens.CornerRadius.pill),
)

@Composable
fun MaaGf2Theme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = Typography(),
        shapes = MaaGf2Shapes,
        content = content,
    )
}
