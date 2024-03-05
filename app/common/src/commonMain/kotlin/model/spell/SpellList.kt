package com.rnett.spellbook.model.spell

import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.rnett.spellbook.ui.theme.components.color

@Composable
fun SpellList(spellList: SpellList) {
    SuggestionChip(
        {},
        { Text(spellList.name) },
        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = spellList.color())
    )
}