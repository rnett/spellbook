package com.rnett.spellbook.ui.pages.spellbooks.spellcasting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rnett.spellbook.model.spellbook.Spellcasting
import com.rnett.spellbook.ui.components.DropdownSelector
import com.rnett.spellbook.ui.components.PlaceholderTransformation
import com.rnett.spellbook.ui.pages.spellbooks.SpellbookEditScreenModel
import kotlinx.collections.immutable.toPersistentList

private enum class SpellcastingType(val builder: SpellcastingBuilder) {
    Spontaneous(SpontaneousSpellcastingBuilder),
    Prepared(PreparedSpellcastingBuilder(false)),
    Flexible(PreparedSpellcastingBuilder(true)),
    Captivator(CaptivatorSpellcastingBuilder);
}

sealed interface SpellcastingBuilder {
    @Composable
    fun Render(addButton: @Composable (Spellcasting?) -> Unit)
}

class AddSpellcastingScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = SpellbookEditScreenModel.model()
        val spellbook by viewModel.spellbook
        val types = remember(spellbook) {
            SpellcastingType.entries.run {
                if (spellbook.spellcastings.any { it is Spellcasting.Captivator })
                    this.filterNot { it != SpellcastingType.Captivator }
                else
                    this
            }
        }

        Column(Modifier.onKeyEvent {
            if (it.type == KeyEventType.KeyDown && it.key == Key.Escape) {
                navigator.pop()
                return@onKeyEvent true
            }
            false

        }) {
            var spellcastingType by remember { mutableStateOf<SpellcastingType?>(null) }

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Add a way to cast spells")
                Spacer(Modifier.width(20.dp))
                DropdownSelector<SpellcastingType>(
                    spellcastingType,
                    { Text("Spellcasting type") },
                    visualTransformation = PlaceholderTransformation
                ) {
                    spellcastingType = it
                }
            }

            Spacer(Modifier.height(20.dp))

            HorizontalDivider()

            Spacer(Modifier.height(20.dp))

            spellcastingType?.let {
                Column(Modifier.padding(10.dp)) {
                    it.builder.Render {
                        AddButton(it)
                    }
                }
            }
        }
    }
}


@Composable
private fun AddButton(spellcasting: Spellcasting?) {
    val viewModel = SpellbookEditScreenModel.model()
    val navigator = LocalNavigator.currentOrThrow

    Button(
        {
            if (spellcasting != null) {
                viewModel.update { it.copy(spellcastings = it.spellcastings.toPersistentList().add(spellcasting)) }
                navigator.pop()
            }
        },
        enabled = spellcasting != null
    ) {
        Text("Add spellcasting")
    }
}