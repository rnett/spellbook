package com.rnett.spellbook.components

import androidx.compose.foundation.ClickableText
import androidx.compose.foundation.Image
import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayout
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredHeightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.layout.WithConstraints
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


@OptIn(ExperimentalLayout::class)
@Composable
fun SpellView(spell: Spell, setSidebar: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MainColors.spellBorderColor.asCompose()
    ) {

        Column {

            Row(Modifier.fillMaxWidth().padding(10.dp)
                .clickable(indication = null, interactionState = remember { InteractionState() }) { expanded = !expanded }) {
                Row(Modifier.fillMaxWidth(0.15f)) { Text(spell.name, fontWeight = FontWeight.Bold) }


                Row(Modifier.fillMaxWidth(0.2f).preferredHeight(20.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
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

                    if(spell.actions.hasTrigger){
                        Text("*", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Row(Modifier.fillMaxWidth(0.3f), horizontalArrangement = Arrangement.SpaceEvenly) {
                    spell.lists.forEach {
                        SpellTag(it.name, TagColors.SpellList(it))
                    }
                }

                Row(Modifier.fillMaxWidth(0.2f), horizontalArrangement = Arrangement.SpaceEvenly){
                    spell.school?.let {
                        SpellTag(it.name, TagColors.School)
                    }
                }

                Row(Modifier.fillMaxWidth(0.3f).weight(0.5f), horizontalArrangement = Arrangement.SpaceEvenly){
                    if(spell.requiresAttackRoll)
                        SpellTag("Attack", TagColors.Attack.Attack)

                    spell.save?.let {
                        val name = it.name + if(spell.basicSave) "*" else ""
                        SpellTag(name, TagColors.Attack.Save(it))
                    }
                }

                Box(Modifier.fillMaxWidth(0.1f)) { Text("${spell.type} ${spell.level}", Modifier.align(Alignment.CenterEnd), fontWeight = FontWeight.Bold) }
            }

            Surface(Modifier.fillMaxSize(), color = MainColors.spellBodyColor.asCompose()) {

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