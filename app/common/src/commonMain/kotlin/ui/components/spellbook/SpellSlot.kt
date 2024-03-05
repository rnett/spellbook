package com.rnett.spellbook.ui.components.spellbook

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.model.spell.SpellList
import com.rnett.spellbook.model.spellbook.SpellAtRank
import com.rnett.spellbook.ui.cart.LocalCart
import com.rnett.spellbook.ui.components.spell.SpellInfo
import com.rnett.spellbook.ui.components.spell.SpellInfoCard
import com.rnett.spellbook.utils.ifLet
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun SpellSlot(
    spellAtRank: SpellAtRank?,
    spellLists: ImmutableSet<SpellList>,
    edit: ((SpellAtRank?) -> Unit)? = null
) {
    val cart = LocalCart.current
    if (spellAtRank == null) {
        SpellInfoCard(Modifier.ifLet<Modifier>(edit != null) {
            it.clickable(cart.selectedSpell != null) { cart.selectedSpell?.let { edit!!(SpellAtRank(it, null)) } }
        }) {
            spellLists.forEach {
                SpellList(it)
            }
        }
    } else {
        SpellInfo(
            spellAtRank.spell,
            enableCart = false,
            prefix = {
                Text(spellAtRank.rank?.toString() ?: "R")
                Spacer(Modifier.width(5.dp))
            },
            suffix = {
                if (edit != null) {
                    IconButton({ edit(null) }) {
                        Icon(Icons.Default.Clear, "Clear spell")
                    }
                }
            }
        )
    }
}