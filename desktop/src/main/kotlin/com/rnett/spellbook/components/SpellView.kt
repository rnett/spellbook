package com.rnett.spellbook.components

import androidx.compose.foundation.ClickableText
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.selection.SelectionContainer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rnett.spellbook.Actions
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.Spell
import com.rnett.spellbook.TagColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.constantActionImg
import com.rnett.spellbook.uninterestingConditions


@OptIn(ExperimentalLayout::class)
@Composable
fun SpellView(spell: Spell, setSidebar: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MainColors.spellBorderColor.asCompose()
    ) {

        Column {

            Column(Modifier.fillMaxWidth().padding(10.dp)
                .clickable(indication = null, interactionState = remember { InteractionState() }) { expanded = !expanded }) {

                Row(Modifier.fillMaxHeight(0.5f)) {
                    Row(Modifier.fillMaxWidth(0.15f).preferredWidthIn(min = 200.dp)) { Text(spell.name, fontWeight = FontWeight.Bold) }


                    Row(
                        Modifier.fillMaxWidth(0.2f).preferredHeight(20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (val actions = spell.actions) {
                            is Actions.Constant -> {
                                Icon(imageFromResource(constantActionImg(actions.actions)))
                            }
                            is Actions.Variable -> {
                                Row {
                                    Icon(imageFromResource(constantActionImg(actions.min)))
                                    Icon(Icons.Default.ArrowForward, Modifier.padding(horizontal = 3.dp))
                                    Icon(imageFromResource(constantActionImg(actions.max)))
                                }
                            }
                            is Actions.Reaction -> {
                                Icon(imageFromResource("static/reaction.png"))
                            }
                            is Actions.Time -> {
                                Icon(imageFromResource("static/time.png"))
                            }
                        }

                        if (spell.actions.hasTrigger) {
                            Text("*", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }

                    Row(Modifier.fillMaxWidth(0.3f), horizontalArrangement = Arrangement.SpaceEvenly) {
                        spell.lists.forEach {
                            SpellTag(it.name, TagColors.SpellList(it))
                        }
                    }

                    Row(Modifier.fillMaxWidth(0.2f), horizontalArrangement = Arrangement.SpaceEvenly) {
                        spell.school?.let {
                            SpellTag(it.name, TagColors.School)
                        }
                    }

                    Row(Modifier.fillMaxWidth(0.3f).weight(0.5f), horizontalArrangement = Arrangement.SpaceEvenly) {
                        if (spell.requiresAttackRoll)
                            SpellTag("Attack", TagColors.Attack.Attack)

                        spell.save?.let {
                            val name = it.name + if (spell.basicSave) "*" else ""
                            SpellTag(name, TagColors.Attack.Save(it))
                        }
                    }

                    Row(Modifier.weight(0.5f), horizontalArrangement = Arrangement.SpaceEvenly) {
                        spell.conditions.forEach {
                            if (it.name !in uninterestingConditions)
                                SpellTag(
                                    it.name,
                                    when (it.positive) {
                                        true -> TagColors.Condition.Positive
                                        false -> TagColors.Condition.Negative
                                        null -> TagColors.Condition.Negative
                                    },
                                    Modifier.clickable {
                                        setSidebar("https://2e.aonprd.com/Conditions.aspx?ID=${it.aonId}")
                                    }
                                )
                        }
                    }

                    Box(Modifier.fillMaxWidth(0.15f)) {
                        Text(
                            "${spell.type} ${spell.level}",
                            Modifier.align(Alignment.CenterEnd),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Divider(Modifier.padding(vertical = 10.dp), color = MainColors.spellBodyColor.asCompose())

                Row(Modifier.fillMaxHeight(0.5f)) {
                    Column(Modifier.fillMaxWidth(0.2f)) {
                        if (spell.duration == null) {
                            SpellTag("Instant", TagColors.Duration.Instant)
                        } else {
                            val duration = spell.duration!!.trim().capitalize()

                            if (spell.sustained) {
                                if (duration == "Sustained") {
                                    SpellTag(color = TagColors.Duration.Sustained) {
                                        Icon(imageFromResource("static/sustained.png"), Modifier.preferredHeight(20.dp))
                                    }
                                } else {
                                    SpellTag(color = TagColors.Duration.Sustained) {
                                        val times = mutableListOf<String>()
                                        val text = Regex("sustained (for )?up to ([\\w ]+)", RegexOption.IGNORE_CASE).replace(duration) {
                                            times += it.groupValues[2]
                                            "\$\$||\$\$"
                                        }

                                        val parts = text.split("\$\$")
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            parts.forEachIndexed { idx, it ->
                                                if (it == "||") {
                                                    var modifier = Modifier.preferredHeight(16.dp)

                                                    Icon(imageFromResource("static/sustained.png"), modifier)
                                                    Text("(${times.removeFirst()})")
                                                } else
                                                    Text(it)
                                            }
                                        }
                                    }
                                }
                            } else {
                                SpellTag(duration, TagColors.Duration.NonSustained)
                            }
                        }
                    }
                }
            }

            Surface(Modifier.fillMaxWidth(), color = MainColors.spellBodyColor.asCompose()) {

                val annotatedText = HtmlText(spell.description)

                if (expanded) {
                    SelectionContainer {
                        ClickableText(
                            annotatedText,
                            style = TextStyle(color = MainColors.textColor.asCompose()),
                            modifier = Modifier.fillMaxSize().padding(20.dp)
                        ) {
                            val link = annotatedText.getStringAnnotations("URL", it, it).firstOrNull()?.item
                            if (link != null) {
                                if (link.startsWith("https://2e.aonprd.com/")) {
                                    setSidebar(link)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}