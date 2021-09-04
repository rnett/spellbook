package com.rnett.spellbook.components.sidebar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.onEscape

//TODO do focus handling, closing at top level

@Composable
fun SidebarSurface(
    header: (@Composable BoxScope.() -> Unit)?,
    close: () -> Unit,
    canFocus: Boolean = true,
    modifier: Modifier = Modifier,
    body: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier
            .onEscape { close() },
        contentColor = MainColors.textColor.asCompose(),
        color = MainColors.infoBoxColor.asCompose()
    ) {
        Column(Modifier.fillMaxSize().padding(top = 12.dp)) {
            if (header != null) {
                Box(Modifier.padding(start = 12.dp, end = 12.dp)) {
                    Surface(
                        Modifier.fillMaxWidth(),
                        color = MainColors.infoHeaderColor.asCompose(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(Modifier.padding(vertical = 4.dp, horizontal = 20.dp)) {
                            header()
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
            body()
        }
    }
}