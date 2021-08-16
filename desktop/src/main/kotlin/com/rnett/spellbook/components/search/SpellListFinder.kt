package com.rnett.spellbook.components.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.rnett.spellbook.FilterColors
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.SavedSearchs
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.CenterPopup
import com.rnett.spellbook.components.IconWithTooltip

@Composable
fun SpellListFinder(
    savedSearchs: SavedSearchs,
    update: (SavedSearchs) -> Unit,
    cancel: () -> Unit,
    load: (String) -> Unit,
) {
    val scrollState = rememberScrollState()
    var managing by remember { mutableStateOf(false) }

    Row(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.padding(vertical = 20.dp)) {
                Button(
                    cancel, Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = FilterColors.cancelReset.asCompose())
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Cancel, "Cancel")
                        Spacer(Modifier.width(4.dp))
                        Text("Cancel")
                    }
                }
            }

            Column(
                Modifier.fillMaxWidth().verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Divider(color = FilterColors.dividerColor.asCompose())
                savedSearchs.keys.forEach {
                    Row(Modifier.fillMaxWidth().clickable { load(it) }.padding(10.dp)) {
                        Text(it)
                        Spacer(Modifier.weight(0.5f))
                        IconWithTooltip(Icons.Default.Search, "Search")
                    }
                    Divider(color = FilterColors.dividerColor.asCompose())
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                {
                    managing = true
                },
                Modifier.fillMaxWidth(0.8f),
                colors = ButtonDefaults.buttonColors(backgroundColor = FilterColors.goToFullPage.asCompose())
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Bookmarks, "Manage Saved Searches")
                    Spacer(Modifier.width(4.dp))
                    Text("Manage Saved Searches")
                }
            }
        }

        val scrollStyle = LocalScrollbarStyle.current.let { it.copy(unhoverColor = it.hoverColor, thickness = 12.dp) }
        VerticalScrollbar(
            rememberScrollbarAdapter(scrollState),
            style = scrollStyle
        )
    }

    if (managing) {
        ManageSavedSearchesPopup(
            { managing = false },
            savedSearchs,
            update,
            {
                managing = false
                load(it)
            }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ManageSavedSearchesPopup(
    close: () -> Unit,
    filters: SavedSearchs,
    update: (SavedSearchs) -> Unit,
    search: (String) -> Unit
) {
    Popup(
        CenterPopup,
        onDismissRequest = { close() },
        focusable = true
    ) {
        Surface(
            Modifier.fillMaxSize(0.9f),
            color = MainColors.outsideColor.withAlpha(0.6f).asCompose().compositeOver(Color.Black),
            border = BorderStroke(2.dp, Color.Black),
            elevation = 5.dp
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(close) {
                        IconWithTooltip(Icons.Default.Close, "Close")
                    }
                }
                SavedSearchPage(filters, update, search)
            }
        }
    }
}