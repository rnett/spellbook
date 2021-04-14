package com.rnett.spellbook.components

import androidx.compose.foundation.ScrollbarStyleAmbient
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.FilterColors
import com.rnett.spellbook.asCompose

@Composable
fun SpellListFinder(savedNames: List<String>, cancel: () -> Unit, load: (String) -> Unit) {
    val scrollState = rememberScrollState()
    Row(Modifier.fillMaxWidth()) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.padding(vertical = 10.dp)) {
                Button(cancel, Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(backgroundColor = FilterColors.cancelReset.asCompose())) {
                    Row {
                        Icon(Icons.Outlined.Cancel, "Cancel")
                        Text("Cancel")
                    }
                }
            }

            Divider(color = FilterColors.dividerColor.asCompose())
            savedNames.forEach {
                Row(Modifier.fillMaxWidth().clickable { load(it) }.padding(10.dp)) {
                    Text(it)
                    Spacer(Modifier.weight(0.5f))
                    Icon(Icons.Default.Search, "Search")
                }
                Divider(color = FilterColors.dividerColor.asCompose())
            }
        }

        val scrollStyle = ScrollbarStyleAmbient.current.let { it.copy(unhoverColor = it.hoverColor, thickness = 12.dp) }
        VerticalScrollbar(rememberScrollbarAdapter(scrollState),
            style = scrollStyle)
    }
}