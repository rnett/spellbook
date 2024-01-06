package com.rnett.spellbook.components

import com.bnorm.react.RFunction
import com.rnett.spellbook.Save
import kotlinx.css.*
import kotlinx.css.properties.TextDecoration
import materialui.components.chip.chip
import react.RBuilder
import styled.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

object TagColors {
    object Attack {
        object Save {
            val Will = Color.gold
            val Reflex = Color.steelBlue
            val Fortitude = Color.green
        }

        val Attack = Color.crimson
    }
}

object SpellTagStyle : StyleSheet("SpellTag") {
    val spellTag by css {
        padding(5.px, 10.px)
        fontWeight = FontWeight.bold
        borderRadius = 16.px
        marginTop = 10.px
        fontFamily = "Helvetica"
    }
}

fun CSSBuilder.spellTagStyles(color: Color, textColor: Color) {
    +SpellTagStyle.spellTag
    backgroundColor = color
    this.color = textColor
//    width = 1.rem
}

@OptIn(ExperimentalContracts::class)
@RFunction
fun RBuilder.SpellTagGroup(tags: RBuilder.() -> Unit) {
    contract {
        callsInPlace(tags, InvocationKind.EXACTLY_ONCE)
    }

    styledLi {
        css {
            flexGrow = 1.0
            textAlign = TextAlign.center
            verticalAlign = VerticalAlign.middle
            display = Display.flex
            justifyContent = JustifyContent.spaceEvenly
            flexBasis = FlexBasis("100%")
            flexWrap = FlexWrap.wrap
            marginTop = -10.px
            alignContent = Align.baseline
        }
        tags()
    }
}

@RFunction
fun RBuilder.SpellTag(text: String, color: Color, textColor: Color = Color.white, link: String? = null) {
    if (link == null) {
        styledSpan {
            css {
                spellTagStyles(color, textColor)
            }
            +text
        }
    } else {
        styledA {
            css {
                textDecoration = TextDecoration.none
                this.color = Color.black
                spellTagStyles(color, textColor)
            }
            attrs.href = link
            +text
        }
    }
}

@RFunction
fun RBuilder.SaveTag(save: Save) {
    SpellTag(
        save.name, when (save) {
            Save.Fortitude -> TagColors.Attack.Save.Fortitude
            Save.Reflex -> TagColors.Attack.Save.Reflex
            Save.Will -> TagColors.Attack.Save.Will
        }
    )
}

@RFunction
fun RBuilder.AttackTag() {
    SpellTag("Attack", TagColors.Attack.Attack)
}

