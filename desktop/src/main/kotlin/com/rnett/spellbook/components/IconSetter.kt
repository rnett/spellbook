package com.rnett.spellbook.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconSetter(n: Int, set: (Int) -> Unit, icon: @Composable () -> Unit) {
    IconMaxSetter(n, n, set, icon, icon)
}

@OptIn(ExperimentalFoundationApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun IconMaxSetter(
    n: Int,
    max: Int,
    setMax: (Int) -> Unit,
    selectedIcon: @Composable () -> Unit,
    icon: @Composable () -> Unit
) {
    var editing by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    key(editing) {
        SideEffect {
            if (editing)
                focusRequester.requestFocus()
            else
                focusRequester.freeFocus()
        }
    }

    Row(
        Modifier.focusRequester(focusRequester).onFocusChanged {
            if (editing && !it.isFocused) {
                editing = false
            }
        }.focusable()
            .onPreviewKeyEvent {
                if (it.type == KeyEventType.KeyDown) {
                    when (it.key) {
                        Key.Escape -> {
                            editing = false
                            true
                        }
                        Key.Enter, Key.NumPadEnter -> {
                            editing = !editing
                            true
                        }
                        Key.Plus, Key.DirectionRight -> {
                            setMax(max + 1)
                            true
                        }
                        Key.Minus, Key.DirectionLeft -> {
                            setMax(max - 1)
                            true
                        }
                        else -> false
                    }
                } else false
            }, verticalAlignment = Alignment.CenterVertically
    ) {
        if (editing) {
            IconButtonHand({ setMax(max - 1) }, Modifier.size(20.dp), enabled = (max > 0 && max > n)) {
                IconWithTooltip(Icons.Default.Remove, "Remove")
            }
            Spacer(Modifier.width(4.dp))
        }

        Row(
            Modifier.handPointer().combinedClickable(
                remember { MutableInteractionSource() },
                indication = null,
                onDoubleClick = {
                    editing = !editing
                }
            ) { },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (max > 0 || editing) {
                repeatJoin(max, { Spacer(Modifier.width(4.dp)) }) {
                    if (it < n) {
                        selectedIcon()
                    } else {
                        icon()
                    }
                }
            } else {
                IconWithTooltip(Icons.Default.Remove, "None")
            }
        }

        if (editing) {
            Spacer(Modifier.width(4.dp))
            IconButtonHand({ setMax(max + 1) }, Modifier.size(20.dp)) {
                IconWithTooltip(Icons.Default.Add, "Add")
            }
        }
    }
}