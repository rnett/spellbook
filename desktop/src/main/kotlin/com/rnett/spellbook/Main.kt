package com.rnett.spellbook

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.WindowEvents
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.window.MenuBar
import com.rnett.spellbook.components.SpellListPage
import com.rnett.spellbook.db.SpellbookDB
import java.awt.image.BufferedImage
import javax.swing.SwingUtilities

fun MaximizeWindow(
    title: String = "JetpackDesktopWindow",
    size: IntSize = IntSize(1920, 1024),
    location: IntOffset = IntOffset(-1920, 0),
    centered: Boolean = true,
    icon: BufferedImage? = null,
    menuBar: MenuBar? = null,
    undecorated: Boolean = false,
    resizable: Boolean = true,
    events: WindowEvents = WindowEvents(),
    onDismissRequest: (() -> Unit)? = null,
    content: @Composable () -> Unit = {},
) = SwingUtilities.invokeLater {
    AppWindow(
        title = title,
        size = size,
        location = location,
        centered = centered,
        icon = icon,
        menuBar = menuBar,
        undecorated = undecorated,
        resizable = resizable,
        events = events,
        onDismissRequest = onDismissRequest
    ).also { it.maximize() }.show {
        content()
    }
}

fun main() {
    SpellbookDB.initH2()
    MaximizeWindow("Spellbook") {
        Column(Modifier.fillMaxSize()) {
            TopAppBar() {
                Box(Modifier.fillMaxSize()) {
                    Text("Hello top", modifier = Modifier.align(Alignment.Center))
                }
            }

            SpellListPage()

        }
    }
}