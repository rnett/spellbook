package com.rnett.spellbook.components

import com.bnorm.react.RFunction
import com.bnorm.react.RKey
import com.rnett.spellbook.Spell
import com.rnett.spellbook.uninterestingConditions
import kotlinx.css.*
import kotlinx.css.properties.*
import react.RBuilder
import styled.*

val spellBorderColor = Color.sienna

@RFunction
fun RBuilder.SpellDisplay(spell: Spell, @RKey key: String = spell.name) {
    styledDiv {
        css {
            borderRadius = 15.px
            marginBottom = 20.px
            border(5.px, BorderStyle.solid, spellBorderColor)
        }

        SpellHeader(spell)

        styledDiv {
            css {
                padding(20.px)
            }
            styledP {
                css {
                    height = 5.rem
                }
            }
        }
    }
}

@RFunction
fun RBuilder.SpellHeader(spell: Spell, @RKey key: String = spell.name) {
    styledDiv {
        css {
            display = Display.flex
            justifyContent = JustifyContent.spaceBetween
            backgroundColor = spellBorderColor
            padding(horizontal = 10.px)
            paddingTop = 2.5.px
            paddingBottom = 8.px
        }

        styledSpan {
            css {
                fontWeight = FontWeight.bold
                paddingTop = 3.px
                width = 12.rem
            }

            styledA {
                css {
                    textDecoration = TextDecoration.none
                    color = Color.black
                }
                attrs.href = "https://2e.aonprd.com/Spells.aspx?ID=${spell.aonId}"
                +spell.name
            }
        }

        styledSpan {
            css {
                declarations["order"] = 2
                fontWeight = FontWeight.bold
                paddingTop = 3.px
                width = 5.rem
                textAlign = TextAlign.right
            }
            +"${spell.type} ${spell.level}"
        }

        // tags

        styledUl {
            css {
                display = Display.flex
                tableLayout = TableLayout.fixed
                flexGrow = 1.0
                margin(0.px)
                padding(0.px)
            }

            SpellTagGroup {
                if (spell.requiresAttackRoll)
                    AttackTag()

                if (spell.save != null)
                    SaveTag(spell.save)
            }

            SpellTagGroup {
                spell.conditions.forEach {
                    if (it.name !in uninterestingConditions)
                        ConditionTag(it)
                }
            }

            SpellTagGroup {

            }

            SpellTagGroup {

            }

            SpellTagGroup {

            }
        }
    }
}