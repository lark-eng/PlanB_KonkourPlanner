package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

enum class AppThemeMode(val title: String) {
    SYSTEM("پیروی از سیستم"),
    DARK("حالت تیره"),
    LIGHT("حالت روشن")
}

data class AccentColorOption(
    val id: String,
    val name: String,
    val color: Color
)

val ACCENT_COLOR_OPTIONS = listOf(
    AccentColorOption("electric_blue", "آبی الکتریک", Color(0xFF38BDF8)),
    AccentColorOption("royal_blue", "آبی کاربنی", Color(0xFF2563EB)),
    AccentColorOption("cyan", "فیروزه‌ای", Color(0xFF06B6D4)),
    AccentColorOption("emerald", "سبز زمردی", Color(0xFF10B981)),
    AccentColorOption("purple", "بنفش رویایی", Color(0xFF8B5CF6)),
    AccentColorOption("pink", "سرخابی / صورتی", Color(0xFFEC4899)),
    AccentColorOption("amber", "طلایی / کهربایی", Color(0xFFF59E0B)),
    AccentColorOption("orange", "نارنجی پرانرژی", Color(0xFFF97316)),
    AccentColorOption("crimson", "قرمز یاقوتی", Color(0xFFEF4444)),
    AccentColorOption("indigo", "نیلی / ایندیگو", Color(0xFF6366F1)),
    AccentColorOption("teal", "سبز آبی (Teal)", Color(0xFF14B8A6)),
    AccentColorOption("lime", "لیمویی فسفری", Color(0xFF84CC16))
)

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.SYSTEM,
    accentColor: Color = ElectricBlue,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemDark
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
    }

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = accentColor,
            onPrimary = DarkBackground,
            primaryContainer = accentColor.copy(alpha = 0.2f),
            onPrimaryContainer = Color.White,
            secondary = accentColor,
            onSecondary = DarkBackground,
            tertiary = PurpleAccent,
            onTertiary = TextPrimary,
            background = DarkBackground,
            onBackground = TextPrimary,
            surface = DarkSurface,
            onSurface = TextPrimary,
            surfaceVariant = DarkSurfaceElevated,
            onSurfaceVariant = TextSecondary,
            outline = DarkCardBorder,
            outlineVariant = DarkDivider
        )
    } else {
        lightColorScheme(
            primary = accentColor,
            onPrimary = Color.White,
            primaryContainer = accentColor.copy(alpha = 0.15f),
            onPrimaryContainer = LightTextPrimary,
            secondary = accentColor,
            onSecondary = Color.White,
            tertiary = PurpleAccent,
            onTertiary = LightTextPrimary,
            background = LightBackground,
            onBackground = LightTextPrimary,
            surface = LightSurface,
            onSurface = LightTextPrimary,
            surfaceVariant = LightSurfaceElevated,
            onSurfaceVariant = LightTextSecondary,
            outline = LightCardBorder,
            outlineVariant = LightDivider
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        // Enforce RTL layout for Persian calendar and texts
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            content()
        }
    }
}
