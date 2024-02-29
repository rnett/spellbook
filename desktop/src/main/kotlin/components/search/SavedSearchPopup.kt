package com.rnett.spellbook.components.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Add
import androidx.compose.material3.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.rnett.spellbook.LocalMainState
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.SavedSearchColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.SmallTextField
import com.rnett.spellbook.components.filter.SpellFilterEditor
import com.rnett.spellbook.components.onEnter
import com.rnett.spellbook.components.onEscape
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.ifLet

@OptIn(ExperimentalAnimationApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun SavedSearchPage(
    search: (String) -> Unit,
) {
    val mainState = LocalMainState.current
    val filters by mainState.savedFilters()

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
                    filters.remove(deletePopupFor!!)
                    mainState.updateSavedFilters(filters.remove(deletePopupFor!!))
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

//                    val focusRequester = FocusRequester()

                    fun closeEditing() {
                        editingName?.let {
                            if (it !in filters.all) {
                                rename(name, it)
                                mainState.updateSavedFilters(rename(name, it))
                            }
                            editingName = null
                        }
//                        focusRequester.freeFocus()
                    }

                    Row(
                        Modifier.fillMaxWidth()
                            .ifLet(name == openFilter) {
                                it.background(
                                    MainColors.outsideColor.withAlpha(0.85f).asCompose().compositeOver(Color.White)
                                )
                            }
//                            .focusRequester(focusRequester)
//                            .onFocusChanged {
//                                if (!it.hasFocus) {
//                                    closeEditing()
//                                }
//                            }
//                            .focusTarget()
//                            .combinedClickable(
//                                onClick = {
//                                    focusRequester.requestFocus()
//                                    openFilter = name
//                                },
//                                onDoubleClick = {
//                                    focusRequester.requestFocus()
//                                    editingName = name
//                                }
//                            )
                            .onEscape {
                                closeEditing()
                            }.onEnter {
                                closeEditing()
                            }
                            .padding(vertical = 10.dp, horizontal = 5.dp),
                        verticalAlignment = Alignment.CenterVertically) {

                        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            if (editingName == null) {
                                Text(name, overflow = TextOverflow.Ellipsis, maxLines = 1, fontWeight = FontWeight.Bold)
                            } else {
                                IconButton({
                                    val newName = editingName!!
                                    editingName = null
                                    rename(name, newName)
                                    mainState.updateSavedFilters(rename(name, newName))
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
                        val newName = filters.newName()
                        filters.replace(newName, SpellFilter())
                        mainState.updateSavedFilters(filters.replace(newName, SpellFilter()))
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
                                    filters.replace(
                                        openFilter,
                                        it
                                    )
                                    mainState.updateSavedFilters(
                                        filters.replace(
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DeletePopup(deleteName: String, close: () -> Unit, remove: () -> Unit) {
    Popup(
        alignment = Alignment.Center, offset = IntOffset(0, -200), focusable = true, onDismissRequest = { close() },
        onPreviewKeyEvent = onEscape(close)
    ) {
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