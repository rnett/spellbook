package com.rnett.spellbook.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun RequiredText(text: String) {
    Row {
        Text(text)
        Text(" *", color = Color.Red)
    }
}