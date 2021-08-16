package com.rnett.spellbook.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.TurnedIn
import androidx.compose.material.icons.filled.TurnedInNot
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.FilterColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.SmallTextField
import com.rnett.spellbook.filter.SpellFilter

@Composable
fun SpellListSaver(
    filter: SpellFilter,
    savedFilters: Map<SpellFilter, String>,
    savedNames: Set<String>,
    newName: () -> String,
    saveFilter: (String, SpellFilter) -> Unit,
    load: () -> Unit,
) {
    Row(Modifier.padding(vertical = 5.dp).height(30.dp), verticalAlignment = Alignment.CenterVertically) {
        if (filter in savedFilters) {
            Spacer(Modifier.width(12.dp))
            IconWithTooltip(Icons.Default.TurnedIn, "Saved")

            Spacer(Modifier.width(4.dp))
            Text(savedFilters.getValue(filter))

            Spacer(Modifier.weight(0.5f))
            IconButton({
                load()
            }) {
                IconWithTooltip(Icons.Default.Bookmarks, "Saved Searches")
            }
        } else {
            var filterName: String? by remember { mutableStateOf(null) }
            if (filterName == null) {
                IconButton({
                    filterName = newName()
                }, enabled = filter != SpellFilter()) {
                    IconWithTooltip(Icons.Default.TurnedInNot, "Save")
                }
                Spacer(Modifier.weight(0.5f))
                IconButton({
                    load()
                }) {
                    IconWithTooltip(Icons.Default.Bookmarks, "Saved Searches")
                }
            } else {
                IconButton({
                    filterName = null
                }) {
                    IconWithTooltip(Icons.Outlined.Cancel, "Cancel")
                }

                SmallTextField(
                    filterName!!, { filterName = it },
                    Modifier.weight(0.5f),
                    isError = filterName!! in savedNames,
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = FilterColors.dividerColor.asCompose(),
                        errorCursorColor = FilterColors.dividerColor.asCompose(),
                        focusedIndicatorColor = FilterColors.dividerColor.asCompose()
                    )
                )

                IconButton({
                    saveFilter(filterName!!, filter)
                    filterName = null
                }, enabled = filterName!! !in savedNames) {
                    IconWithTooltip(Icons.Default.BookmarkAdd, "Save")
                }
            }
        }
    }
}