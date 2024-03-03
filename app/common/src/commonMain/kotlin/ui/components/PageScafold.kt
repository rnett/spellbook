package com.rnett.spellbook.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.ui.sidebar.Sidebar

@Composable
fun PageScaffold(topBar: @Composable () -> Unit, content: @Composable () -> Unit, sidebar: Sidebar?) {
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth()) {
            topBar()
        }
        Spacer(Modifier.fillMaxWidth().height(2.dp).background(MaterialTheme.colorScheme.outline))
        Row(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxHeight().fillMaxWidth(sidebar?.let { 1f - sidebar.width } ?: 1f).weight(1f)
                .padding(10.dp)) {
                content()
            }

            AnimatedVisibility(
                sidebar != null,
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it }
            ) {
                Spacer(Modifier.fillMaxHeight().width(2.dp).background(MaterialTheme.colorScheme.outline))
                Column(
                    Modifier.fillMaxHeight()
                        .fillMaxWidth(sidebar?.width ?: Sidebar.defaultWidth)
                        .widthIn(max = 100.dp)
                        .padding(start = 2.dp)
                ) {
                    sidebar?.render()
                }
            }
        }
    }
}