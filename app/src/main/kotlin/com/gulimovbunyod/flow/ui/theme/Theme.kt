package com.gulimovbunyod.flow.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NightColors = darkColorScheme(
    primary = FlameOrange,
    onPrimary = NightBackground,
    background = NightBackground,
    onBackground = NightOnSurface,
    surface = NightSurface,
    onSurface = NightOnSurface,
)

private val DayColors = lightColorScheme(
    primary = FlameDeep,
    onPrimary = DayOnPrimary,
    background = DayBackground,
    onBackground = DayOnSurface,
    surface = DaySurface,
    onSurface = DayOnSurface,
)

/**
 * KENGAYTIRISH NUQTASI: darkTheme qiymati 5-bosqichda Sozlamalar (DataStore)dan keladi.
 * Default: tun.
 */
@Composable
fun FlowTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) NightColors else DayColors,
        content = content,
    )
}
