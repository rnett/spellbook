package com.rnett.spellbook.ui.pages.spellbook

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rnett.spellbook.data.SpellbookReference
import com.rnett.spellbook.model.spellbook.Spellbook
import com.rnett.spellbook.ui.components.IconButtonWithTooltip
import com.rnett.spellbook.ui.components.spellbook.edit.SpellbookEditor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun SpellbookEditPage(spellbookReference: SpellbookReference, close: () -> Unit) {
    var loaded by remember { mutableStateOf<Spellbook?>(null) }

    LaunchedEffect(spellbookReference) {
        loaded = spellbookReference.load()
    }
    Column(Modifier.fillMaxSize()) {

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(loaded?.name ?: "Loading...")
            Spacer(Modifier.weight(1f))
            IconButtonWithTooltip(Icons.Default.Close, "Close", onCLick = close)
        }

        val scope = rememberCoroutineScope()

        DisposableEffect(spellbookReference) {
            onDispose { GlobalScope.launch { loaded?.let { spellbookReference.save(it) } } }
        }

        loaded?.let { spellbook ->
            SpellbookEditor(spellbook, {
                loaded = it
                scope.launch { spellbookReference.save(it) }
            })

        } ?: Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(Modifier.fillMaxSize(0.5f)) }
    }


}