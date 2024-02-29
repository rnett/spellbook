package com.rnett.spellbook.components.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.BookmarkAdd
import androidx.compose.material3.icons.filled.Bookmarks
import androidx.compose.material3.icons.filled.TurnedIn
import androidx.compose.material3.icons.filled.TurnedInNot
import androidx.compose.material3.icons.outlined.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.FilterColors
import com.rnett.spellbook.LocalMainState
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.IconButtonHand
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.SmallTextField
import com.rnett.spellbook.filter.SpellFilter

@Composable
fun SpellFilterSaver(
    filter: SpellFilter,
    adjustFilter: (SpellFilter) -> SpellFilter,
    load: () -> Unit,
    leftContent: @Composable () -> Unit
) {
    val mainState = LocalMainState.current
    val savedFilters by mainState.savedFilters()
    val savedFiltersByFilter by remember { derivedStateOf { savedFilters.entries.associate { it.value to it.key } } }
    val savedNames by remember { derivedStateOf { savedFilters.keys } }

    Row(Modifier.padding(vertical = 5.dp).height(30.dp), verticalAlignment = Alignment.CenterVertically) {
        if (filter in savedFiltersByFilter) {
            Spacer(Modifier.width(12.dp))
            IconWithTooltip(Icons.Default.TurnedIn, "Saved")

            Spacer(Modifier.width(4.dp))
            Text(savedFiltersByFilter.getValue(filter))

            Spacer(Modifier.weight(0.5f))
            IconButtonHand({
                load()
            }) {
                IconWithTooltip(Icons.Default.Bookmarks, "Saved Searches")
            }
            leftContent()
        } else {
            var filterName: String? by remember { mutableStateOf(null) }
            if (filterName == null) {
                IconButtonHand({
                    filterName = savedFilters.newName()
                }, enabled = filter != SpellFilter()) {
                    IconWithTooltip(Icons.Default.TurnedInNot, "Save")
                }
                Spacer(Modifier.weight(0.5f))
                IconButtonHand({
                    load()
                }) {
                    IconWithTooltip(Icons.Default.Bookmarks, "Saved Searches")
                }
                leftContent()
            } else {
                IconButtonHand({
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

                IconButtonHand({
                    mainState.saveFilter(filterName!!, adjustFilter(filter))
                    filterName = null
                }, enabled = filterName!! !in savedNames) {
                    IconWithTooltip(Icons.Default.BookmarkAdd, "Save")
                }
            }
        }
    }
}