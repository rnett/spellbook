package com.rnett.spellbook.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.BoxWithTooltip
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerIcon
import androidx.compose.ui.semantics.Role
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
import java.awt.Desktop
import java.net.URI


fun openInBrowser(url: String) {
    if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(URI(url))
    }
}

fun Int.ordinalWord(): String {
    val word = this.toString()
    if (this in 11..19)
        return word + "th"

    return word + when (word.last()) {
        '1' -> "st"
        '2' -> "nd"
        '3' -> "rd"
        else -> "th"
    }
}

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

class AbsolutePopupPositionProvider(val position: IntOffset) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset = position

}

@Composable
fun <T> Iterable<T>.join(separator: @Composable () -> Unit, render: @Composable (T) -> Unit) {
    var first = true
    forEach {
        if (!first)
            separator()
        key(it) {
            render(it)
        }
        first = false
    }
}

@Composable
fun repeatJoin(n: Int, separator: @Composable () -> Unit, render: @Composable (Int) -> Unit) {
    var first = true
    repeat(n) {
        if (!first)
            separator()
        key(it) {
            render(it)
        }
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

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.focusableEditable(
    editing: Boolean,
    setEditing: (Boolean) -> Unit
): Modifier = composed {

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(editing) {
        if (editing)
            focusRequester.requestFocus()
        else
            focusRequester.freeFocus()
    }

    Modifier.focusRequester(focusRequester).focusable().onKeyEvent {
        if (it.type == KeyEventType.KeyDown) {
            when (it.key) {
                Key.Escape -> {
                    setEditing(false)
                    true
                }
                Key.Enter, Key.NumPadEnter -> {
                    setEditing(!editing)
                    true
                }
                else -> false
            }
        } else false
    }
}

fun Modifier.onKeyDown(keys: Set<Key>, consume: Boolean = true, handler: () -> Unit): Modifier {
    return onKeyEvent {
        if (it.type == KeyEventType.KeyDown && it.key in keys) {
            handler()
            consume
        } else false
    }
}

fun Modifier.onKeyDown(key: Key, consume: Boolean = true, handler: () -> Unit) = onKeyDown(setOf(key), consume, handler)

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.onEscape(handler: () -> Unit) = onKeyDown(Key.Escape) {
    handler()
}

@OptIn(ExperimentalComposeUiApi::class)
inline fun onEscape(crossinline handler: () -> Unit): (KeyEvent) -> Boolean = {
    if (it.type == KeyEventType.KeyDown && it.key == Key.Escape) {
        handler()
        true
    } else false
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.onEnter(handler: () -> Unit) = onKeyDown(setOf(Key.Enter, Key.NumPadEnter)) {
    handler()
}

fun Modifier.clickableNoIndication(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
) = composed {
    Modifier.clickable(remember { MutableInteractionSource() }, null, enabled, onClickLabel, role, onClick)
}