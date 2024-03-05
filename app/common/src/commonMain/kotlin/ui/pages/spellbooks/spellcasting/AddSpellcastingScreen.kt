package com.rnett.spellbook.ui.pages.spellbooks.spellcasting

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rnett.spellbook.model.spellbook.Spellcasting
import com.rnett.spellbook.ui.pages.spellbooks.EditScreen
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

private enum class SpellcastingType(
    val exclusive: Boolean,
    val createSpec: () -> SpellcastingSpec<*>,
    val leadingIcon: (@Composable () -> Unit)? = null
) {
    Focus(true, { SpellcastingSpec.Focus }),
    Items(true, { SpellcastingSpec.Items }),
    Stave(false, { SpellcastingSpec.Stave }),
    Spontaneous(false, { SpellcastingSpec.Spontaneous }),
    Prepared(false, { SpellcastingSpec.Prepared });
}

private fun Spellcasting.type(): SpellcastingType = when (this) {
    is Spellcasting.Focus -> SpellcastingType.Focus
    is Spellcasting.Items -> SpellcastingType.Items
    is Spellcasting.Prepared -> SpellcastingType.Prepared
    is Spellcasting.Spontaneous -> SpellcastingType.Spontaneous
    is Spellcasting.Stave -> SpellcastingType.Stave
}

private sealed class SpellcastingSpec<T : Spellcasting>(val type: SpellcastingType) {

    open val isValid: Boolean = true

    abstract fun build(): T

    data object Focus : SpellcastingSpec<Spellcasting.Focus>(SpellcastingType.Focus) {
        override fun build(): Spellcasting.Focus = Spellcasting.Focus(persistentListOf())
    }

    //TODO make work
    data object Stave : SpellcastingSpec<Spellcasting.Focus>(SpellcastingType.Focus) {
        override fun build(): Spellcasting.Focus = Spellcasting.Focus(persistentListOf())
    }

    data object Items : SpellcastingSpec<Spellcasting.Focus>(SpellcastingType.Focus) {
        override fun build(): Spellcasting.Focus = Spellcasting.Focus(persistentListOf())
    }

    data object Spontaneous : SpellcastingSpec<Spellcasting.Focus>(SpellcastingType.Focus) {
        override fun build(): Spellcasting.Focus = Spellcasting.Focus(persistentListOf())
    }

    data object Prepared : SpellcastingSpec<Spellcasting.Focus>(SpellcastingType.Focus) {
        override fun build(): Spellcasting.Focus = Spellcasting.Focus(persistentListOf())
    }
}

class AddSpellcastingScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = EditScreen.editScreenModel()
        val spellbook = viewModel.loadedSpellbook.spellbook

        Column {
            var spellcastingSpec by remember { mutableStateOf<SpellcastingSpec<*>?>(null) }

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Add a way to cast spells")
                Spacer(Modifier.width(20.dp))

                var dropdownExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    dropdownExpanded && (spellcastingSpec == null),
                    { dropdownExpanded = it && (spellcastingSpec == null) }) {
                    TextField(
                        value = spellcastingSpec?.type?.name.orEmpty(),
                        label = { Text("Spellcasting type") },
                        onValueChange = {},
                        enabled = spellcastingSpec == null,
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(dropdownExpanded, { dropdownExpanded = false }) {
                        SpellcastingType.entries.forEach { type ->
                            DropdownMenuItem(
                                { Text(type.name) },
                                {
                                    dropdownExpanded = false
                                    spellcastingSpec = type.createSpec()
                                },
                                leadingIcon = type.leadingIcon,
                                contentPadding = PaddingValues(3.dp),
                                enabled = (!type.exclusive) || spellbook.spellcastings.none { it.type() == type }
                            )
                        }
                    }
                }
                Spacer(Modifier.width(20.dp))
                IconButton({
                    spellcastingSpec = null
                }) {
                    Icon(Icons.Default.Clear, "Clear")
                }
                Spacer(Modifier.weight(1f))
                IconButton({ navigator.pop() }) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, "Back")
                }
            }

            Spacer(Modifier.height(20.dp))

            HorizontalDivider()

            Spacer(Modifier.height(20.dp))

            spellcastingSpec?.let {
                Column {
                    SpellcastingSpecEditor(it) { spellcastingSpec = it }
                    Spacer(Modifier.height(20.dp))
                    AddButton(it.isValid) { it.build() }
                }
            }
        }
    }
}

@Composable
private fun <T : Spellcasting> ColumnScope.SpellcastingSpecEditor(
    spec: SpellcastingSpec<T>,
    update: (SpellcastingSpec<T>) -> Unit
) {
    Row { Text(spec.type.name, style = MaterialTheme.typography.headlineSmall) }

    when (spec) {
        SpellcastingSpec.Focus -> Text("No configuration necessary")
        SpellcastingSpec.Items -> Text("No configuration necessary")
        SpellcastingSpec.Prepared -> Text("TODO Items")
        SpellcastingSpec.Spontaneous -> Text("TODO Items")
        SpellcastingSpec.Stave -> Text("TODO Items")
    }
}

@Composable
private fun AddButton(enabled: Boolean, builder: () -> Spellcasting) {
    val build by rememberUpdatedState(builder)
    val viewModel = EditScreen.editScreenModel()
    val navigator = LocalNavigator.currentOrThrow

    Button(
        {
            viewModel.update { it.copy(spellcastings = it.spellcastings.toPersistentList().add(build())) }
            navigator.pop()
        },
        enabled = enabled
    ) {
        Text("Add spellcasting")
    }
}