package com.rnett.spellbook.components.core

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import kotlin.math.max
import kotlin.math.min

@Composable
fun ScaleDensityBy(scaleFactor: Float, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalDensity.provides(LocalDensity.current.let {
        Density(it.density * scaleFactor, it.fontScale)
    }), content = content)
}

@Composable
fun ScaleDensityToHeight(target: Float, min: Float? = null, max: Float? = null, content: @Composable () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        var scaleFactor = constraints.maxHeight.toFloat() / target
        if (min != null)
            scaleFactor = max(scaleFactor, min)
        if (max != null)
            scaleFactor = min(scaleFactor, max)
        ScaleDensityBy(scaleFactor, content)
    }
}