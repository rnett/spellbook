package com.rnett.spellbook.components.spell

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.AonUrl
import com.rnett.spellbook.components.SidebarNav
import com.rnett.spellbook.components.core.HtmlText
import com.rnett.spellbook.components.core.WithOriginalDensity
import com.rnett.spellbook.data.allSpells
import com.rnett.spellbook.spell.Spell
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

@Composable
//@Preview
internal fun PreviewSpell() {
    SpellDisplay(allSpells.first { it.name == "Fireball" }, null, true, {})
}

@Composable
fun LabeledText(label: String, text: String) {
    Text(buildAnnotatedString {
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        append("$label: ")
        pop()
        append(text)
    })
}

@Composable
fun SpellBody(spell: Spell) {

    val annotatedText = HtmlText(spell.description)

    Column(
        Modifier.fillMaxWidth().padding(
            top = 10.dp,
            bottom = 20.dp,
            start = 20.dp,
            end = 20.dp
        )
    ) {
        WithOriginalDensity {
            ProvideTextStyle(TextStyle.Default.copy(color = MainColors.textColor.asCompose(), fontSize = 0.8.em)) {

                spell.actions.trigger?.let {
                    LabeledText("Trigger", it)
                }

                Row {

                    spell.range?.let {
                        LabeledText("Range", it)
                        Spacer(Modifier.width(8.dp))
                    }

                    spell.targets?.let {
                        LabeledText("Targets", it)
                    }
                }

                spell.area?.let {
                    LabeledText("Area", it)
                }

                Divider(
                    Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    color = MainColors.spellBorderColor.asCompose()
                )

                val sidebar = SidebarNav.currentSidebar()
                ClickableText(
                    annotatedText,
                    style = LocalTextStyle.current,
                    modifier = Modifier.fillMaxWidth()
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SpellDisplay(spell: Spell, setSelectedSpell: ((Spell) -> Unit)?, expanded: Boolean, headerClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MainColors.spellBorderColor.asCompose()
    ) {

        Column(Modifier) {
            SpellHeader(
                spell,
                Modifier.clickable(remember { MutableInteractionSource() }, null) { headerClick() },
                setSelectedSpell
            )

            Surface(Modifier.fillMaxWidth(), color = MainColors.spellBodyColor.asCompose()) {

                AnimatedVisibility(expanded) {

                    if (expanded) {
                        SpellBody(spell)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SpellDisplay(spell: Spell, setSelectedSpell: ((Spell) -> Unit)?, globalExpanded: Flow<Boolean>) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(spell) {
        globalExpanded.collect {
            expanded = it
        }
    }

    SpellDisplay(spell, setSelectedSpell, expanded) { expanded = !expanded }
}