package com.rnett.spellbook

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Image Viewer",
        state = WindowState(WindowPlacement.Maximized),
    ) {
        MainPage()
    }
}