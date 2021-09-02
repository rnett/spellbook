package com.rnett.spellbook.components.spell

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.AonUrl
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.SidebarNav
import com.rnett.spellbook.components.core.FlowRow
import com.rnett.spellbook.components.core.HtmlText
import com.rnett.spellbook.components.core.WithOriginalDensity
import com.rnett.spellbook.components.openInBrowser
import com.rnett.spellbook.components.ordinalWord
import com.rnett.spellbook.spell.Creature
import com.rnett.spellbook.spell.Heightening
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.Summons


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
private fun SpellBodyText(text: String) {

    val sidebar = SidebarNav.currentSidebar()

    val htmlText = HtmlText(text)

    ClickableText(
        htmlText,
        style = LocalTextStyle.current,
        modifier = Modifier.fillMaxWidth()
    ) {
        val link = htmlText.getStringAnnotations("URL", it, it).firstOrNull()?.item
        if (link != null) {
            if (link.startsWith("https://2e.aonprd.com/")) {
                sidebar(AonUrl(link))
            }
        }
    }
}

@Composable
private fun SpellBodyDivider() {
    Divider(
        Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 8.dp),
        color = MainColors.spellBorderColor.asCompose()
    )
}

@Composable
private fun HeighteningDisplay(heightening: Heightening) {
    when (heightening) {
        is Heightening.Every -> {
            LabeledText("Heightened (+${heightening.every})", heightening.heighten)
        }
        is Heightening.Specific -> {
            Column {
                heightening.heightening.forEach { (level, heighten) ->
                    LabeledText("Heightened (${level.ordinalWord()})", heighten)
                }
            }
        }
    }
}

@Composable
private fun CreatureDisplay(creature: Creature) {
    val sidebar = SidebarNav.currentSidebar()

    ClickableText(
        AnnotatedString(creature.name),
        style = LocalTextStyle.current.copy(fontStyle = FontStyle.Italic, textDecoration = TextDecoration.Underline)
    ) {
        sidebar(AonUrl(creature))
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun SummonsDisplay(summons: Summons) {
    when (summons) {
        is Summons.Multiple -> {
            var expanded by remember { mutableStateOf(false) }
            Row(
                Modifier.clickable { expanded = !expanded }.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Summons", Modifier.padding(bottom = 2.dp), fontWeight = FontWeight.Bold, fontSize = 1.2.em)
                Spacer(Modifier.width(5.dp))

                if (expanded) {
                    IconWithTooltip(Icons.Default.ExpandLess, "Collapse")
                } else {
                    IconWithTooltip(Icons.Default.ExpandMore, "Expand")
                }
            }
            AnimatedVisibility(
                expanded,
                enter = fadeIn() + expandVertically(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    summons.summons.entries.sortedBy { it.key }.forEach {
                        Text("Level ${it.key}", fontWeight = FontWeight.Bold)
                        FlowRow(horizontalGap = 10.dp, verticalGap = 10.dp) {
                            it.value.forEach {
                                CreatureDisplay(it)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
        is Summons.Single -> {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Summons:", fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(4.dp))
                CreatureDisplay(summons.creature)
            }
        }
    }
}

@Composable
fun SpellBody(spell: Spell) {

    Box(
        Modifier.fillMaxWidth().padding(
            top = 10.dp,
            bottom = 20.dp,
            start = 20.dp,
            end = 20.dp
        ),
        contentAlignment = Alignment.TopStart
    ) {
        WithOriginalDensity {
            ProvideTextStyle(TextStyle.Default.copy(color = MainColors.textColor.asCompose(), fontSize = 0.8.em)) {
                Row(
                    Modifier
                        .align(Alignment.TopEnd)
                        .clickable {
                            openInBrowser(AonUrl(spell).url)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Open AoN")
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Filled.OpenInNew, "Open in browser", Modifier.height(20.dp))
                }

                Column {
                    Column(Modifier.heightIn(min = 20.dp)) {
                        spell.actions.trigger?.let {
                            LabeledText("Trigger", it.replace("\n", ""))
                        }

                        spell.requirements?.let {
                            LabeledText("Requirements", it.replace("\n", ""))
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
                    }

                    SpellBodyDivider()

                    SpellBodyText(spell.description)

                    if (spell.heightening != null) {
                        SpellBodyDivider()
                        HeighteningDisplay(spell.heightening!!)
                    }

                    if (spell.summons != null) {
                        SpellBodyDivider()
                        SummonsDisplay(spell.summons!!)
                    }

                    if (spell.postfix != null) {
                        SpellBodyDivider()
                        SpellBodyText(spell.postfix!!)
                    }
                }
            }
        }
    }
}