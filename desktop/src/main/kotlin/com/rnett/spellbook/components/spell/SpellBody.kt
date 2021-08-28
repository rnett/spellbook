package com.rnett.spellbook.components.spell

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.rnett.spellbook.components.ordinalWord
import com.rnett.spellbook.spell.Heightening
import com.rnett.spellbook.spell.Spell


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
fun SpellBody(spell: Spell) {

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

                SpellBodyDivider()

                SpellBodyText(spell.description)

                if (spell.heightening != null) {
                    SpellBodyDivider()
                    HeighteningDisplay(spell.heightening!!)
                }

                //TODO summons

                if (spell.postfix != null) {
                    SpellBodyDivider()
                    SpellBodyText(spell.postfix!!)
                }
            }
        }
    }
}