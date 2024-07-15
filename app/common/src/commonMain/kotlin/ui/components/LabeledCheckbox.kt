package com.rnett.spellbook.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LabeledCheckbox(
    checked: Boolean,
    onUpdate: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    labelAfter: Boolean = true,
    label: @Composable () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (!labelAfter)
            label()
        Checkbox(checked, onUpdate, modifier, enabled, colors)
        if (labelAfter)
            label()
    }
}