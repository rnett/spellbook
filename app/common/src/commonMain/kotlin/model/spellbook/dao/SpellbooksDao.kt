package com.rnett.spellbook.model.spellbook.dao

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.rnett.spellbook.data.SpellbooksDao

@Composable
fun SpellbooksDao(dao: SpellbooksDao) {
    val leadingIcon = dao.display.leadingIcon
    val trailingIcon = dao.display.trainingIcon
    Row(verticalAlignment = Alignment.CenterVertically) {
        leadingIcon?.invoke()
        Text(dao.name)
        trailingIcon?.invoke()
    }
}