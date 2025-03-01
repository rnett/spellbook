package com.rnett.spellbook.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun IconButtonWithTooltip(
    icon: ImageVector,
    text: String,
    tint: Color = LocalContentColor.current,
    enabled: Boolean = true,
    shape: Shape = IconButtonDefaults.smallRoundShape,
    onCLick: () -> Unit
) {
    TooltipBox(
        rememberTooltipPositionProvider(),
        {
            RichTooltip { Text(text) }
        },
        rememberTooltipState(),
        focusable = false
    ) {
        IconButton(onCLick, shape = shape) {
            Icon(icon, tint = tint, contentDescription = text)
        }
    }
}