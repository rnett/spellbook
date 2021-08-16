package com.rnett.spellbook.components.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.rnett.spellbook.*
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.SmallTextField
import com.rnett.spellbook.components.filter.SpellFilterEditor
import com.rnett.spellbook.filter.SpellFilter

@OptIn(ExperimentalAnimationApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun SavedSearchPage(
    filters: SavedSearchs,
    update: (SavedSearchs) -> Unit,
    search: (String) -> Unit,
) {

    Surface(color = MainColors.outsideColor.asCompose(), contentColor = MainColors.textColor.asCompose()) {
        Row {
            var openFilter: String? by remember { mutableStateOf(if (filters.isEmpty()) null else filters.keys.first()) }
            val scrollState = rememberScrollState()

            var deletePopupFor: String? by remember(filters) { mutableStateOf(null) }

            if (deletePopupFor != null) {
                DeletePopup(deletePopupFor!!, { deletePopupFor = null }, {
                    if (deletePopupFor == openFilter) {
                        openFilter = if (filters.size == 1)
                            null
                        else {
                            val list = filters.keys.toList()
                            if (deletePopupFor == list.last())
                                list.first()
                            else
                                list[list.indexOf(deletePopupFor!!) + 1]
                        }
                    }
                    update(filters.remove(deletePopupFor!!))
                })
            }

            fun rename(old: String, new: String) =
                filters.rename(old, new).also {
                    if (openFilter == old)
                        openFilter = new
                }


            Column(Modifier.padding(start = 10.dp, top = 10.dp).weight(0.5f).verticalScroll(scrollState)) {

                filters.all.forEach { (name, _) ->
                    var editingName: String? by remember { mutableStateOf(null) }

                    val focusRequester = FocusRequester()

                    Row(
                        Modifier.fillMaxWidth()
                            .ifLet(name == openFilter) {
                                it.background(
                                    MainColors.outsideColor.withAlpha(0.85f).asCompose().compositeOver(Color.White)
                                )
                            }
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                if (!it.hasFocus) {
                                    editingName?.let {
                                        if (it !in filters.all)
                                            update(rename(name, it))
                                        editingName = null
                                    }
                                }
                            }
                            .focusTarget()
                            .combinedClickable(
                                onClick = {
                                    focusRequester.requestFocus()
                                    openFilter = name
                                },
                                onDoubleClick = {
                                    focusRequester.requestFocus()
                                    editingName = name
                                }
                            )
                            .padding(vertical = 10.dp, horizontal = 5.dp),
                        verticalAlignment = Alignment.CenterVertically) {

                        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            if (editingName == null) {
                                Text(name, overflow = TextOverflow.Ellipsis, maxLines = 1, fontWeight = FontWeight.Bold)
                            } else {
                                IconButton({
                                    val newName = editingName!!
                                    editingName = null
                                    update(rename(name, newName))
                                }, enabled = editingName == name || editingName!! !in filters.all) {
                                    IconWithTooltip(Icons.Default.CheckCircleOutline, "Save")
                                }
                                Spacer(Modifier.width(5.dp))
                                SmallTextField(
                                    editingName!!, { editingName = it },
                                    isError = editingName!! in filters.all,
                                    singleLine = true,
                                    colors = TextFieldDefaults.textFieldColors(
                                        cursorColor = FilterColors.dividerColor.asCompose(),
                                        errorCursorColor = FilterColors.dividerColor.asCompose(),
                                        focusedIndicatorColor = FilterColors.dividerColor.asCompose()
                                    )
                                )
                            }
                        }

                        Row(
                            Modifier.fillMaxWidth(0.4f),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            IconButton({
                                deletePopupFor = name
                            }) {
                                IconWithTooltip(
                                    Icons.Outlined.DeleteForever,
                                    "Delete",
                                    tint = Color.Red.copy(alpha = 0.7f)
                                )
                            }

                            Spacer(Modifier.weight(1f))

                            IconWithTooltip(Icons.Default.ArrowForwardIos, "Open")
                        }
                    }
                    Divider()
                }

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    IconButton({
                        val newName = filters.newName
                        update(filters.set(newName, SpellFilter()))
                        openFilter = newName
                    }) {
                        IconWithTooltip(Icons.Default.Add, "New Search")
                    }
                }
            }
            VerticalScrollbar(rememberScrollbarAdapter(scrollState))
            Column(Modifier.weight(0.5f)) {
                AnimatedVisibility(openFilter != null) {
                    openFilter?.let { openFilter ->
                        key(openFilter) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Spacer(Modifier.height(10.dp))
                                Button(
                                    { search(openFilter) }, Modifier.fillMaxWidth(0.8f),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = SavedSearchColors.searchButtonColor.asCompose())
                                ) {
                                    Row {
                                        Icon(Icons.Outlined.Search, "Search")
                                        Text("Search")
                                    }
                                }
                                Spacer(Modifier.height(10.dp))
                                SpellFilterEditor(filters[openFilter]!!, showReset = false) {
                                    update(
                                        filters.set(
                                            openFilter,
                                            it
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeletePopup(deleteName: String, close: () -> Unit, remove: () -> Unit) {
    Popup(alignment = Alignment.Center, offset = IntOffset(0, -200), focusable = true, onDismissRequest = { close() }) {
        Surface(
            Modifier
                .width(300.dp),
            shape = RoundedCornerShape(5.dp),
            color = MainColors.outsideColor.withAlpha(0.6f).asCompose().compositeOver(Color.Black),
            border = BorderStroke(2.dp, Color.Black),
            elevation = 20.dp
        ) {
            Column(
                Modifier.fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Delete saved search \"$deleteName\"?",
                    textAlign = TextAlign.Center,
                    color = MainColors.textColor.asCompose()
                )

                Spacer(Modifier.height(20.dp))

                Row(Modifier.fillMaxWidth()) {
                    Button(
                        {
                            remove()
                            close()
                        },
                        Modifier.weight(0.5f),
                        colors = ButtonDefaults.buttonColors(
                            MainColors.outsideColor.withAlpha(0.8f).asCompose().compositeOver(Color.Red)
                        )
                    ) {
                        Text("Delete", textAlign = TextAlign.Center)
                    }
                    Button(
                        {
                            close()
                        },
                        Modifier.weight(0.5f),
                        colors = ButtonDefaults.buttonColors(MainColors.outsideColor.asCompose())
                    ) {
                        Text("Cancel", textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}