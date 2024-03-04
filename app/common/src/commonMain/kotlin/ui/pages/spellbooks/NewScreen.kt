package com.rnett.spellbook.ui.pages.spellbooks

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.rnett.spellbook.data.LoadedSpellbook
import com.rnett.spellbook.data.SpellbookDaoLoader
import com.rnett.spellbook.data.SpellbooksDao
import com.rnett.spellbook.model.spellbook.Spellbook
import com.rnett.spellbook.model.spellbook.SpellbookPreview
import com.rnett.spellbook.model.spellbook.dao.DaoSelector
import com.rnett.spellbook.ui.spellbook.LocalSpellbook
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class NewScreen : Screen {

    @Composable
    override fun Content() {
        val daos = SpellbookDaoLoader.daos
        val (selectedDao, selectDao) = remember { mutableStateOf(daos.firstOrNull()) }
        Column(Modifier.fillMaxSize()) {
            DaoSelector("Load a spellbook from...", selectedDao, selectDao)

            Spacer(Modifier.height(20.dp))

            HorizontalDivider()

            val spellbook = LocalSpellbook.current
            val navigator = LocalNavigator.current!!

            AnimatedContent(selectedDao) {
                Column(Modifier.fillMaxSize()) {
                    if (it != null) {
                        Spacer(Modifier.height(20.dp))

                        Creator(it) {
                            spellbook.loadedSpellbook = it
                            navigator.plusAssign(EditScreen())
                        }

                        Spacer(Modifier.height(20.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(20.dp))

                        SpellbookLoader(it) {
                            spellbook.loadedSpellbook = it
                            navigator.plusAssign(EditScreen())
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpellbookLoader(dao: SpellbooksDao, load: (LoadedSpellbook) -> Unit) {

    Surface {
        Column(Modifier.padding(10.dp)) {
            Text("Load a spellbook")
            Spacer(Modifier.height(10.dp))

            var listing by remember { mutableStateOf<List<LoadedSpellbook>?>(null) }
            LaunchedEffect(dao) {
                while (isActive) {
                    listing = dao.listSpellbooks()
                    delay(1.seconds)
                }
            }

            val scope = rememberCoroutineScope()

            AnimatedContent(listing) {
                if (it == null) {
                    CircularProgressIndicator()
                } else {
                    if (it.isEmpty()) {
                        Text("No stored spellbooks")
                    } else {

                        var deleting by remember { mutableStateOf<Spellbook?>(null) }

                        deleting?.let { deletingSpellbook ->
                            AlertDialog(
                                { deleting = null },
                                confirmButton = {
                                    TextButton(
                                        {
                                            GlobalScope.launch {
                                                dao.delete(deletingSpellbook.name)
                                                scope.launch { listing = dao.listSpellbooks() }
                                            }
                                        }
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Outlined.Delete, "Delete spellbook", tint = Color.Red)
                                            Spacer(Modifier.width(5.dp))
                                            Text(
                                                "Delete",
                                                color = Color.Red
                                            )
                                        }
                                    }
                                },
                                dismissButton = { TextButton({ deleting = null }) { Text("Cancel") } },
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Outlined.Delete, "Delete spellbook", tint = Color.Red)
                                        Spacer(Modifier.width(5.dp))
                                        Text("Really delete spellbook \"${deletingSpellbook.name}\"?")
                                    }
                                },
                                text = { Text("Are you sure you want to delete the spellbook \"${deletingSpellbook.name}\"? This will permanently delete all of its data.") }
                            )
                        }

                        LazyColumn(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(it, { it.dao.name + "/" + it.name }) {
                                SpellbookPreview(
                                    it.spellbook,
                                    Modifier.clickable { load(it) },
                                    buttons = {
                                        IconButton({ deleting = it.spellbook }) {
                                            Icon(Icons.Outlined.Delete, "Delete spellbook", tint = Color.Red)
                                        }
                                    },
                                    dao = it.dao
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Creator(dao: SpellbooksDao, create: (LoadedSpellbook) -> Unit) {
    var editingName by remember { mutableStateOf("") }
    var nameIsValid by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(editingName) {
        nameIsValid = null
        nameIsValid = dao.isNewNameValid(editingName)
    }

    Surface {
        Column(Modifier.padding(10.dp)) {
            Text("Create a new spellbook")
            Spacer(Modifier.height(10.dp))

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

                fun submit() {
                    if (nameIsValid == true) {
                        val book = Spellbook(editingName, persistentListOf())
                        create(LoadedSpellbook(dao, editingName, book))
                        GlobalScope.launch {
                            dao.saveSpellbook(null, book)
                        }
                        editingName = ""
                        nameIsValid = null
                    }
                }

                IconButton(
                    { submit() },
                    enabled = nameIsValid == true
                ) {
                    Icon(Icons.Default.CheckCircle, "Create")
                }

                Spacer(Modifier.width(10.dp))

                val isError = nameIsValid == false && editingName.isNotEmpty()
                TextField(
                    editingName,
                    {
                        Snapshot.withMutableSnapshot {
                            nameIsValid = null
                            editingName = it
                        }
                    },
                    Modifier.weight(1f),
                    label = { Text("Spellbook name") },
                    isError = isError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    supportingText = if (isError) {
                        { Text("Duplicate or invalid name") }
                    } else null,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            submit()
                        }
                    )
                )
            }
        }

    }
}
