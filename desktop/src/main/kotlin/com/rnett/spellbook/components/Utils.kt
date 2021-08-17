package com.rnett.spellbook.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.BoxWithTooltip
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerIcon
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupPositionProvider
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.ifLet

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

@Composable
fun <T> Iterable<T>.join(separator: @Composable () -> Unit, render: @Composable (T) -> Unit) {
    var first = true
    forEach {
        if (!first)
            separator()
        render(it)
        first = false
    }
}

@Composable
fun repeatJoin(n: Int, separator: @Composable () -> Unit, render: @Composable (Int) -> Unit) {
    var first = true
    repeat(n) {
        if (!first)
            separator()
        render(it)
        first = false
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.handPointer() = pointerIcon(PointerIcon.Hand)

@Composable
fun IconButtonHand(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) = IconButton(onClick, modifier.ifLet(enabled, Modifier::handPointer), enabled, interactionSource, content)