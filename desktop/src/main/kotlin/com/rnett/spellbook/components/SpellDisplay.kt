package com.rnett.spellbook.components

import androidx.compose.foundation.ClickableText
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.selection.SelectionContainer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.Spell
import com.rnett.spellbook.asCompose


@Composable
fun SpellDisplay(spell: Spell) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MainColors.spellBorderColor.asCompose()
    ) {

        Column {
            SpellHeader(spell, Modifier.clickable(indication = null, interactionState = remember { InteractionState() }) { expanded = !expanded })

            Surface(Modifier.fillMaxWidth(), color = MainColors.spellBodyColor.asCompose()) {

                val annotatedText = HtmlText(spell.description)

                if (expanded) {
                    SelectionContainer {
                        val sidebar = SidebarNav.currentSidebar
                        ClickableText(
                            annotatedText,
                            style = TextStyle(color = MainColors.textColor.asCompose()),
                            modifier = Modifier.fillMaxSize().padding(20.dp)
                        ) {
                            val link = annotatedText.getStringAnnotations("URL", it, it).firstOrNull()?.item
                            if (link != null) {
                                if (link.startsWith("https://2e.aonprd.com/")) {
                                    sidebar(AonUrl(link))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}