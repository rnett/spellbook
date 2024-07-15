package com.rnett.spellbook.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.enums.enumEntries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun <T> DropdownSelector(
    entries: Iterable<T>,
    selected: T?,
    crossinline text: (T) -> String,
    noinline label: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    crossinline onSelected: (T) -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        dropdownExpanded && enabled,
        { dropdownExpanded = it }) {
        TextField(
            value = selected?.let(text).orEmpty(),
            label = label,
            enabled = enabled,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true),
            singleLine = true
        )

        ExposedDropdownMenu(dropdownExpanded && enabled, { dropdownExpanded = false }) {
            entries.forEach { item ->
                DropdownMenuItem(
                    { Text(text(item)) },
                    {
                        dropdownExpanded = false
                        onSelected(item)
                    },
                    contentPadding = PaddingValues(3.dp)
                )
            }
        }
    }
}

@Composable
inline fun <reified T : Enum<T>> DropdownSelector(
    selected: T?,
    noinline label: (@Composable () -> Unit)? = null,
    crossinline text: (T) -> String = { it.name },
    enabled: Boolean = true,
    crossinline onSelected: (T) -> Unit
) {
    DropdownSelector(enumEntries<T>(), selected, text, label, enabled, onSelected)
}