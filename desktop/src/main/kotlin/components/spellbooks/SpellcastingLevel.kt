package com.rnett.spellbook.components.spellbooks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.FilterColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.DragSetState
import com.rnett.spellbook.components.spell.DraggingSpell
import com.rnett.spellbook.ifLet
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spellbook.SpellLevel
import com.rnett.spellbook.spellbook.SpellSlotSpec
import com.rnett.spellbook.spellbook.Spellcasting
import com.rnett.spellbook.spellbook.SpellcastingType
import com.rnett.spellbook.spellbook.withLevel


@Composable
fun SpellcastingLevel(
    spellcasting: Spellcasting<*>,
    level: Int,
    set: (Spellcasting<*>) -> Unit,
    openInfoDrawer: (Spell) -> Unit,
    searchSlot: (SpellSlotSpec, (Spell) -> Unit) -> Unit
) {
    @Suppress("NAME_SHADOWING") val set by rememberUpdatedState(set)
    if (level > spellcasting.maxLevel) return

    if (spellcasting.type == SpellcastingType.Spontaneous) {
        SpontaneousLevel(
            spellcasting[level] as SpellLevel.Spontaneous,
            spellcasting.defaultLists,
            level,
            { set(spellcasting.withLevel(level, it)) },
            openInfoDrawer,
            searchSlot
        )
    } else {
        PreparedLevel(
            spellcasting[level] as SpellLevel.Prepared,
            spellcasting.defaultLists,
            level,
            { set(spellcasting.withLevel(level, it)) },
            openInfoDrawer,
            searchSlot
        )
    }
}

abstract class SpellcastingLevelScope(private val columnScope: ColumnScope) : ColumnScope by columnScope {
    @Composable
    abstract fun SpellbookDivider(noStartPadding: Boolean)

    @Composable
    fun SpellbookDivider() = SpellbookDivider(false)
}

@Composable
fun SpellbookStyleDivider(modifier: Modifier = Modifier.fillMaxWidth()) {
    Divider(modifier, color = FilterColors.dividerColor.asCompose().copy(alpha = 0.4f))
}

@Composable
inline fun SpellcastingLevelDisplay(
    dragSet: DragSetState<Spell>,
    headerContent: @Composable RowScope.() -> Unit,
    bodyContent: @Composable SpellcastingLevelScope.() -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        dragSet.display {
            DraggingSpell(it)
        }

        Row(Modifier.fillMaxWidth().height(30.dp), verticalAlignment = Alignment.CenterVertically) {
            headerContent()
        }

        SpellbookStyleDivider()

        Column(Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
            val scope = object : SpellcastingLevelScope(this) {
                @Composable
                override fun SpellbookDivider(noStartPadding: Boolean) {
                    SpellbookStyleDivider(
                        Modifier.fillMaxWidth(0.9f).ifLet(!noStartPadding) { it.padding(start = 10.dp) }
                            .align(Alignment.Start)
                    )
                }

            }
            bodyContent(scope)
        }
    }
}
