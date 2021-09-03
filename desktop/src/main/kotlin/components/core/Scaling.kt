package com.rnett.spellbook.components.core

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import kotlin.math.max
import kotlin.math.min

val LocalOriginalDensity = compositionLocalOf<Density> { error("Not provided") }

@Composable
fun ScaleDensityBy(scaleFactor: Float, content: @Composable () -> Unit) {
    val density = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity.provides(Density(density.density * scaleFactor, density.fontScale)),
        LocalOriginalDensity.providesDefault(density),
        content = content
    )
}

@Composable
fun WithOriginalDensity(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalDensity.provides(LocalOriginalDensity.current),
        content = content
    )
}

@Composable
fun ScaleDensityToHeight(
    target: Float,
    min: Float? = null,
    max: Float? = null,
    offset: Float = 0f,
    scale: Float = 1f,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        var scaleFactor = constraints.maxHeight.toFloat() / target
        scaleFactor = scaleFactor * scale + offset
        if (min != null)
            scaleFactor = max(scaleFactor, min)
        if (max != null)
            scaleFactor = min(scaleFactor, max)
        ScaleDensityBy(scaleFactor, content)
    }
}