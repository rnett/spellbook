package com.rnett.spellbook.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rnett.spellbook.FilterColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.core.FlowRow
import com.rnett.spellbook.filter.Filter
import com.rnett.spellbook.filter.FilterClause
import com.rnett.spellbook.filter.Operation
import com.rnett.spellbook.filter.SpellFilterPart

@Composable
inline fun FilterDivider(end: Boolean = false, start: Boolean = false) {
    if (!start)
        Spacer(Modifier.height(4.dp))
    Divider(color = FilterColors.dividerColor.asCompose())
    Spacer(Modifier.height(4.dp))
    if (end)
        EndSpacer()
}

@Composable
inline fun ClauseDivider(operation: Operation) {
    Spacer(Modifier.height(4.dp))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).background(color = FilterColors.dividerColor.asCompose()).height(1.dp))

        Box(Modifier.padding(horizontal = 10.dp)) {
            OperationBadge(operation)
        }

        Box(Modifier.weight(1f).background(color = FilterColors.dividerColor.asCompose()).height(1.dp))
    }
    Spacer(Modifier.height(4.dp))
}

@Composable
inline fun EndSpacer() {
    Spacer(Modifier.height(10.dp))
}

@Composable
fun OperationBadge(operation: Operation, modifier: Modifier = Modifier) {
    Box(modifier.height(20.dp).padding(4.dp, 1.dp), contentAlignment = Alignment.TopCenter) {
        Text(operation.name, textAlign = TextAlign.Center, fontSize = 14.sp, maxLines = 1)
    }
}

@Composable
fun OperationButton(operation: Operation, set: (Operation) -> Unit) {
    OperationBadge(operation, Modifier.background(color = FilterColors.typeButtonColor.asCompose(), shape = RoundedCornerShape(5.dp))
        .clickable {
            set(when (operation) {
                Operation.AND -> Operation.OR
                Operation.OR -> Operation.AND
            })
        })
}

@Composable
fun NegateButton(negate: Boolean, set: (Boolean) -> Unit) {
    Box(Modifier
        .background(color = if (negate) Color.Red.copy(alpha = 0.5f) else Color.Transparent, shape = RoundedCornerShape(5.dp))
        .border(Dp.Hairline, FilterColors.dividerColor.asCompose().copy(alpha = 0.8f), shape = RoundedCornerShape(5.dp))
        .clickable {
            set(!negate)
        }
        .height(20.dp).padding(4.dp, 1.dp),
        contentAlignment = Alignment.Center) {
        Text("NOT", textAlign = TextAlign.Center, fontSize = 14.sp, maxLines = 1)
    }
}


@Composable
fun OptionalBoolean(current: Boolean?, set: (Boolean?) -> Unit, modifier: Modifier = Modifier, title: @Composable() () -> Unit) {
    Column {
        Spacer(Modifier.height(5.dp))
        Box(modifier.fillMaxWidth()) {
            Box(Modifier.align(Alignment.CenterStart)) {
                title()
            }

            Surface(
                Modifier.align(Alignment.CenterEnd).size(20.dp),
                shape = RoundedCornerShape(3.dp),
                border = BorderStroke(1.5.dp, FilterColors.dividerColor.asCompose()),
                color = when (current) {
                    true -> FilterColors.checkboxRequired.asCompose().copy(alpha = 0.85f)
                    false -> FilterColors.checkboxForbidden.asCompose().copy(alpha = 0.85f)
                    null -> Color.Transparent
                }
            ) {
                IconButton({
                    set(when (current) {
                        false -> null
                        null -> true
                        true -> false
                    })
                }) {
                    when (current) {
                        null -> Icon(Icons.Default.HorizontalRule, "Either")
                        true -> Icon(Icons.Default.Check, "Required")
                        false -> Icon(Icons.Default.Close, "Forbidden")
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        FilterDivider(end = true)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T> SetEditor(
    current: Set<T>,
    allOptions: Set<T>,
    set: (Set<T>) -> Unit,
    expanded: ExpansionManager.Component,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    render: @Composable (T) -> Unit,
) {
    Column(modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().clickable { expanded.expand(!expanded.expanded) }.padding(bottom = 4.dp)) {
            Box(Modifier.align(Alignment.CenterStart)) {
                title()
            }

            IconToggleButton(expanded.expanded, {
                expanded.expand(it)
            }, Modifier.align(Alignment.TopEnd).size(30.dp)) {
                if (expanded.expanded) {
                    Icon(Icons.Default.Done, "Finish Edit")
                } else {
                    Icon(Icons.Default.Edit, "Edit")
                }
            }
        }

        FilterDivider(start = true)

        FlowRow(Modifier.fillMaxWidth(), verticalGap = 8.dp, horizontalGap = 10.dp) {
            current.forEach {
                Box(Modifier.clickable(enabled = expanded.expanded) {
                    set(current - it)
                }) {
                    render(it)
                }
            }
        }


        if (expanded.expanded)
            Spacer(Modifier.height(8.dp))

        AnimatedVisibility(expanded.expanded) {
            Surface(shape = RoundedCornerShape(10.dp),
                color = FilterColors.adderSpaceColor.asCompose(),
                border = BorderStroke(Dp.Hairline, FilterColors.dividerColor.asCompose())) {
                Box(Modifier.padding(5.dp)) {
                    FlowRow(Modifier.fillMaxWidth(), verticalGap = 8.dp, horizontalGap = 10.dp) {
                        (allOptions - current).forEach {
                            Box(Modifier.clickable {
                                set(current + it)
                            }) {
                                render(it)
                            }
                        }
                    }
                }
            }
        }


        if (expanded.expanded)
            Spacer(Modifier.height(8.dp))
        EndSpacer()
    }
}

@OptIn(ExperimentalAnimationApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun <T : SpellFilterPart> FilterEditor(
    current: Filter<T>,
    allOptions: Set<T>,
    set: (Filter<T>) -> Unit,
    expanded: ExpansionManager.Component,
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    render: @Composable (T) -> Unit,
) {
    Column(modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().clickable { expanded.expand(!expanded.expanded) }.padding(bottom = 4.dp)) {
            Box(Modifier.align(Alignment.CenterStart)) {
                title()
            }

            Row(Modifier.align(Alignment.TopEnd).padding(bottom = 5.dp, start = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                NegateButton(current.negate) { set(current.copy(negate = it)) }

                Spacer(Modifier.width(8.dp))
                OperationButton(current.outerOperation) { set(current.copy(outerOperation = it)) }

                Spacer(Modifier.width(8.dp))
                OperationButton(current.clauseOperation) { set(current.copy(clauseOperation = it)) }

                Spacer(Modifier.width(8.dp))
                IconToggleButton(expanded.expanded, {
                    expanded.expand(it)
                }, Modifier.size(30.dp)) {
                    if (expanded.expanded) {
                        Icon(Icons.Default.Done, "Finish Edit")
                    } else {
                        Icon(Icons.Default.Edit, "Edit")
                    }
                }
            }
        }
        FilterDivider(start = true)

        current.clauses.forEachIndexed { idx, it ->

            if (idx != 0)
                ClauseDivider(current.outerOperation)

            Clause(it, current.clauseOperation, allOptions, expanded.expanded, { new ->
                set(current.copy(clauses = current.clauses.mapIndexed { index, old ->
                    if (index == idx)
                        new
                    else
                        old
                }))
            }, render)
        }
        if (expanded.expanded) {
            if (current.clauses.isNotEmpty())
                ClauseDivider(current.outerOperation)

            Clause(FilterClause(), current.clauseOperation, allOptions, expanded.expanded, { new ->
                set(current.copy(clauses = current.clauses.plusElement(new)))
            }, render)
        }
        EndSpacer()
    }
}

@ExperimentalAnimationApi
@Composable
private fun <T> Clause(
    current: FilterClause<T>,
    operation: Operation,
    allOptions: Set<T>,
    expanded: Boolean,
    set: (FilterClause<T>) -> Unit,
    render: @Composable() (T) -> Unit,
) {
    if (current.isEmpty) {
        Spacer(Modifier.height(30.dp))
    } else {
        FlowRow(Modifier.fillMaxWidth().padding(4.dp), verticalGap = 8.dp, horizontalGap = 4.dp, verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.padding(end = 4.dp)) {
                NegateButton(current.negate) { set(current.copy(negate = it)) }
            }

            current.filters.forEachIndexed { i, it ->
                if (i != 0) {
                    OperationBadge(operation)
                }

                Box(Modifier.clickable(enabled = expanded) {
                    set(current - it)
                }) {
                    render(it)
                }
            }
        }
    }

    if (expanded)
        Spacer(Modifier.height(8.dp))

    AnimatedVisibility(expanded) {
        Surface(shape = RoundedCornerShape(10.dp),
            color = FilterColors.adderSpaceColor.asCompose(),
            border = BorderStroke(Dp.Hairline, FilterColors.dividerColor.asCompose())) {
            Box(Modifier.padding(5.dp)) {
                FlowRow(Modifier.fillMaxWidth(), verticalGap = 8.dp, horizontalGap = 10.dp) {
                    (allOptions - current.filters).forEach {
                        Box(Modifier.clickable {
                            set(current + it)
                        }) {
                            render(it)
                        }
                    }
                }
            }
        }
    }


    if (expanded)
        Spacer(Modifier.height(8.dp))
}
