package com.rnett.spellbook.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconSetter(n: Int, set: (Int) -> Unit, icon: @Composable () -> Unit) {
    IconMaxSetter(0, n, set, icon, icon)
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

    Row(
        Modifier
            .onKeyEvent {
                if (it.type == KeyEventType.KeyDown) {
                    when (it.key) {
                        Key.Plus, Key.DirectionRight -> {
                            setMax(max + 1)
                            true
                        }
                        Key.Minus, Key.DirectionLeft -> {
                            if (max > 0 && max > n) {
                                setMax(max - 1)
                                true
                            } else
                                false
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