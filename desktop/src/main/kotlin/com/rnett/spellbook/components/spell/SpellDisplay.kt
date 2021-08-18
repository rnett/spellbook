package com.rnett.spellbook.components.spell

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.AonUrl
import com.rnett.spellbook.components.SidebarNav
import com.rnett.spellbook.components.core.HtmlText
import com.rnett.spellbook.spell.Spell

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SpellDisplay(spell: Spell, setSelectedSpell: ((Spell) -> Unit)?, expanded: Boolean, headerClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MainColors.spellBorderColor.asCompose()
    ) {

        Column {
            SpellHeader(
                spell,
                Modifier.clickable(remember { MutableInteractionSource() }, null) { headerClick() },
                setSelectedSpell
            )

            Surface(Modifier.fillMaxWidth(), color = MainColors.spellBodyColor.asCompose()) {

                val annotatedText = HtmlText(spell.description)

                AnimatedVisibility(expanded) {

                    if (expanded) {
                        //TODO clickable doesn't work inside
//                    SelectionContainer {
                        val sidebar = SidebarNav.currentSidebar()
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
//                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SpellDisplay(spell: Spell, setSelectedSpell: ((Spell) -> Unit)?) {
    var expanded by remember { mutableStateOf(false) }

    SpellDisplay(spell, setSelectedSpell, expanded) { expanded = !expanded }
}