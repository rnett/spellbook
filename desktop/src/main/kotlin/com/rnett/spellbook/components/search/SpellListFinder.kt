package com.rnett.spellbook.components.search

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.*
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