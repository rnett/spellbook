package com.rnett.spellbook.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.BoxWithTooltip
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.PopupPositionProvider
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose

@Composable
fun Tooltip(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.shadow(4.dp),
        color = MainColors.tooltipColor.asCompose(),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(0.5.dp, Color.Black)
    ) {
        Box(Modifier.padding(10.dp, 2.dp)) {
            content()
        }
    }
}

@Composable
fun TextTooltip(text: String) {
    Tooltip { Text(text, fontSize = 12.sp, color = Color.White) }
}

@Composable
fun IconWithTooltip(
    imageVector: ImageVector,
    tooltip: String,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
) {
    BoxWithTooltip({ TextTooltip(tooltip) }) {
        Icon(imageVector, tooltip, modifier, tint)
    }
}

object CenterPopup : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val xExcess = windowSize.width - popupContentSize.width
        val yExcess = windowSize.height - popupContentSize.height
        return IntOffset(xExcess / 2, yExcess / 2)
    }
}
