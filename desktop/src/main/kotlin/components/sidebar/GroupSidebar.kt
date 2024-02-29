package com.rnett.spellbook.components.sidebar

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.ExpandLess
import androidx.compose.material3.icons.filled.ExpandMore
import androidx.compose.material3.icons.filled.UnfoldLess
import androidx.compose.material3.icons.filled.UnfoldMore
import androidx.compose.material3.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.LocalMainState
import com.rnett.spellbook.components.*
import com.rnett.spellbook.components.core.FlowRow
import com.rnett.spellbook.components.spell.ShortSpellDisplay
import com.rnett.spellbook.group.SpellGroup
import com.rnett.spellbook.ifLet
import com.rnett.spellbook.pages.LightSidebarDivider
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.toMutableNamedList
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

//TODO add groups, renameing

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun GroupDisplay(
    name: String,
    group: SpellGroup,
    set: (SpellGroup) -> Unit,
    depth: Int,
    initialExpanded: Boolean,
    globalExpanded: MutableSharedFlow<Boolean>,
    dragFrom: DragSetState<Spell>,
    dragTo: DragSetState<Spell>
) {
    var expanded by remember { mutableStateOf(initialExpanded) }

    LaunchedEffect(globalExpanded) {
        globalExpanded.collect {
            expanded = it
        }
    }

    var isDraggingOver by remember { mutableStateOf(false) }

    val draggingModifier = Modifier.draggableContainer(dragTo,
        onEnter = {
            isDraggingOver = true
        },
        onLeave = {
            isDraggingOver = false
        },
        accepts = { it !in group.spells },
        onDrop = {
            isDraggingOver = false
            set(group.copy(spells = group.spells + it))
            true
        })
        .ifLet(isDraggingOver) {
            it.background(Color.White.copy(alpha = 0.3f))
        }

    Column(
        Modifier.fillMaxWidth()

    ) {
        Row(Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .then(draggingModifier)
            .padding(vertical = 5.dp)) {
            Row(Modifier.fillMaxWidth(0.7f)) {
                Text(name, fontWeight = FontWeight.Bold)
            }

            if (!group.isEmpty()) {
                if (expanded) {
                    IconWithTooltip(Icons.Default.ExpandLess, "Collapse")
                } else {
                    IconWithTooltip(Icons.Default.ExpandMore, "Expand")
                }
            }
        }
        AnimatedVisibility(
            expanded,
            enter = fadeIn() + expandVertically(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                //TODO drag to reorder (subgroups, too)
                Column(draggingModifier) {
                    group.spells.join({ LightSidebarDivider() }) {
                        Row(
                            Modifier
                                .draggableItem(dragFrom, it)
                                .padding(vertical = 2.dp)
                        ) {
                            ShortSpellDisplay(it, Modifier.fillMaxWidth(0.9f), showLevel = true)
                            Spacer(Modifier.weight(1f))
                            IconButtonHand(
                                {
                                    set(group.copy(spells = group.spells - it))
                                },
                                Modifier.height(24.dp)
                            ) {
                                IconWithTooltip(
                                    Icons.Outlined.Close,
                                    "Remove from Group",
                                    tint = Color.Red.copy(alpha = 0.6f)
                                )
                            }
                            Spacer(Modifier.weight(0.1f).widthIn(max = 10.dp))
                        }
                    }
                }
                if (group.subgroups.isNotEmpty()) {
                    if (group.spells.isNotEmpty()) {
                        LightSidebarDivider()
                        Spacer(Modifier.height(10.dp))
                    }

                    Column(Modifier.padding(start = 10.dp)) {
                        group.subgroups.forEach { (name, subgroup) ->
                            GroupDisplay(
                                name,
                                subgroup,
                                {
                                    set(group.copy(subgroups = group.subgroups.toMutableNamedList().apply {
                                        this[name] = it
                                    }))
                                },
                                depth + 1,
                                if (depth == 0) initialExpanded else (initialExpanded && group.spells.isEmpty()),
                                globalExpanded,
                                dragFrom,
                                dragTo
                            )
                            LightSidebarDivider()
                        }
                    }
                }
                if (!group.isEmpty())
                    Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun GroupSidebar(groups: MutableMap<String, SpellGroup>, close: () -> Unit) {
//    val focusRequester = remember { FocusRequester() }
//
//    LaunchedEffect(groups) {
//        focusRequester.requestFocus()
//    }

    val dragTo = LocalMainState.current.dragSpellsToSide
    val dragFrom = LocalMainState.current.dragSpellsFromSide

    val globalExpanded = remember {
        MutableSharedFlow<Boolean>(
            extraBufferCapacity = 3,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    SidebarSurface(
        {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Spell Groups", Modifier.padding(vertical = 10.dp), fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))

                IconButtonHand({
                    globalExpanded.tryEmit(true)
                }, Modifier.size(28.dp)) {
                    IconWithTooltip(Icons.Default.UnfoldMore, "Expand All")
                }
                Spacer(Modifier.width(4.dp))
                IconButtonHand({
                    globalExpanded.tryEmit(false)
                }, Modifier.size(28.dp)) {
                    IconWithTooltip(Icons.Default.UnfoldLess, "Collapse All")
                }
                Spacer(Modifier.width(4.dp))
            }
        },
        close,
//        focusRequester = focusRequester
    ) {
        Column(
            Modifier.fillMaxSize()
                .padding(start = 10.dp)
        ) {
            val scrollState = rememberScrollState()

            var openGroup by remember { mutableStateOf<String?>(null) }

            FlowRow(verticalGap = 6.dp, horizontalGap = 10.dp) {
                groups.keys.forEach { name ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(2.dp, Color.Black),
                        color = Color.DarkGray.copy(alpha = 0.4f).ifLet(openGroup == name) {
                            it.compositeOver(Color.White.copy(alpha = 0.3f))
                        }
                    ) {
                        Text(
                            name, Modifier.clickable {
                                openGroup = name
                            }.padding(vertical = 5.dp, horizontal = 8.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(15.dp))

            LightSidebarDivider()

            Spacer(Modifier.height(15.dp))

            Row(Modifier.fillMaxSize()) {

                Column(
                    Modifier.weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(scrollState)
                ) {
                    openGroup?.let { openGroup ->
                        groups[openGroup]?.let {
                            key(openGroup) {
                                GroupDisplay(
                                    openGroup,
                                    it,
                                    {
                                        groups[openGroup] = it
                                    },
                                    0,
                                    true,
                                    globalExpanded,
                                    dragFrom,
                                    dragTo
                                )
                            }
                        }
                    }
                }

                val scrollStyle =
                    LocalScrollbarStyle.current.let { it.copy(unhoverColor = it.hoverColor, thickness = 12.dp) }
                VerticalScrollbar(
                    rememberScrollbarAdapter(scrollState),
                    style = scrollStyle
                )
            }
        }
    }
}