package com.rnett.spellbook.ui.pages.spellbooks.spellcasting

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rnett.spellbook.model.spellbook.Spellcasting
import com.rnett.spellbook.ui.pages.spellbooks.EditScreen
import kotlinx.collections.immutable.toPersistentList

private enum class SpellcastingType {
    Focus, Stave, Items, Spontaneous, Prepared
}

class AddSpellcastingScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Row(Modifier.fillMaxWidth()) {
            Text("Add a way to cast spells")
            Spacer(Modifier.weight(1f))
            IconButton({ navigator.pop() }) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, "Back")
            }
        }

        Row {
            Spacer(Modifier.width(20.dp))
            IconButton({}) {
                Icon(Icons.Default.Clear, "Clear")
            }
        }
    }
}

@Composable
private fun AddButton(enabled: Boolean, builder: () -> Spellcasting) {
    val build by rememberUpdatedState(builder)
    val viewModel = EditScreen.editScreenModel()
    Button(
        { viewModel.update { it.copy(spellcastings = it.spellcastings.toPersistentList().add(build())) } },
        enabled = enabled
    ) {
        Text("Add spellcasting")
    }
}