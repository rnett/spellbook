package com.rnett.spellbook.components

import com.bnorm.react.RFunction
import com.rnett.spellbook.*
import kotlinx.css.*
import kotlinx.html.LI
import kotlinx.html.title
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

    object Condition {
        val Positive = Color.seaGreen
        val Negative = Color.indianRed
        val Neutral = Color.darkGrey
    }

    object Rarity {
        val Unique = Color("#0c1466")
        val Rare = Color("#0c1466")
        val Uncommon = Color("#c45500")
        val Common = Color.transparent
    }

    val School = Color.darkViolet

    fun SpellList(spellList: SpellList): Color = when (spellList) {
        SpellList.Arcane -> Color.royalBlue
        SpellList.Divine -> Color.gold
        SpellList.Occult -> Color.silver
        SpellList.Primal -> Color.green
        SpellList.Focus -> Color.oliveDrab
        SpellList.Other -> Color.dimGray
    }

    val Trait = Color.brown

    object Duration {
        val Sustained = Color("#08AE58")
        val NonSustained = Color.darkOrange
        val Instant = Color.darkSlateBlue
    }

    object Area {
        fun AreaType(areaType: AreaType): Color = when (areaType) {
            AreaType.Burst -> Color.red
            AreaType.Line -> Color.lightSkyBlue
            AreaType.Emanation -> Color("#FF5733")
            AreaType.Cone -> Color.darkKhaki
            AreaType.Wall -> Color.darkSlateGray
        }

        val Untyped = Color("#BB7D70")
    }

    fun Targeting(targeting: TargetingType): Color = when (targeting) {
        TargetingType.SingleTarget -> Color("#900C3F")
        TargetingType.MultiTarget -> Color("#9e4a1c")
        TargetingType.Other -> Color.darkGray
        TargetingType.Area.Cone -> Color("#448BB3")
        TargetingType.Area.Line -> Color("#3BE8EE")
        TargetingType.Area.Emanation -> Color("#B8A025")
        TargetingType.Area.Burst -> Color.red
        TargetingType.Area.Wall -> Color.darkSlateGray
        TargetingType.Area.Other -> Color("#8F5C5C")
    }
}

object SpellTagStyle : StyleSheet("SpellTag") {
    val spellTag by css {
        padding(5.px, 10.px)
        fontWeight = FontWeight.bold
        borderRadius = 16.px
        fontFamily = "Helvetica"
    }
}

fun CSSBuilder.spellTagStyles(color: Color, textColor: Color) {
    +SpellTagStyle.spellTag
    backgroundColor = color
    this.color = textColor

    if (color != Color.transparent) {
        borderWidth = 1.px
        borderColor = Color.black
    }

//    width = 1.rem
}

@OptIn(ExperimentalContracts::class)
@RFunction
fun RBuilder.SpacingTagGroup(builder: StyledDOMBuilder<LI>.() -> Unit = {}, tags: RBuilder.() -> Unit) {
    contract {
        callsInPlace(tags, InvocationKind.EXACTLY_ONCE)
    }

    styledLi {
        css {
            flexGrow = 1.0
            display = Display.flex
            justifyContent = JustifyContent.spaceEvenly
            flexWrap = FlexWrap.wrap
            flexBasis = FlexBasis("100%")
            alignContent = Align.baseline
        }
        builder()
        tags()
    }
}

@OptIn(ExperimentalContracts::class)
@RFunction
fun RBuilder.CompactTagGroup(builder: StyledDOMBuilder<LI>.() -> Unit = {}, includeMargin: Boolean = true, tags: RBuilder.() -> Unit) {
    contract {
        callsInPlace(tags, InvocationKind.EXACTLY_ONCE)
    }

    styledLi {
        css {
            display = Display.flex
            justifyContent = JustifyContent.flexStart
            flexWrap = FlexWrap.wrap
            alignContent = Align.baseline
            flexShrink = 1.0
        }
        builder()
        tags()
    }
}

@RFunction
fun RBuilder.SpellTag(title: String, color: Color, textColor: Color = Color.white, builder: SPANBuilder) {
    styledSpan {
        css {
            spellTagStyles(color, textColor)
        }
        attrs.title = title
        builder()
    }
}

@RFunction
fun RBuilder.SpellTag(text: String, title: String, color: Color, textColor: Color = Color.white, link: String? = null) {
    if (link == null) {
        SpellTag(title, color, textColor) {
            +text
        }
    } else {
        builtinA(link, title, style = { spellTagStyles(color, textColor) }) {
            +text
        }
    }
}

@RFunction
fun RBuilder.SaveTag(save: Save, basic: Boolean) {
    SpellTag(
        save.name + (if (basic) "" else "*"), "Save: ${if (basic) "Basic " else ""} $save", when (save) {
            Save.Fortitude -> TagColors.Attack.Save.Fortitude
            Save.Reflex -> TagColors.Attack.Save.Reflex
            Save.Will -> TagColors.Attack.Save.Will
        }
    )
}

@RFunction
fun RBuilder.AttackTag() {
    SpellTag("Attack", "Spell Attack", TagColors.Attack.Attack)
}

@RFunction
fun RBuilder.ConditionTag(condition: Condition) {
    SpellTag(
        condition.name, "Condition: ${condition.name}", when (condition.positive) {
            true -> TagColors.Condition.Positive
            false -> TagColors.Condition.Negative
            null -> TagColors.Condition.Negative
        }, link = "https://2e.aonprd.com/Conditions.aspx?ID=${condition.aonId}"
    )
}

@RFunction
fun RBuilder.RarityTag(rarity: Rarity) {
    SpellTag(
        if (rarity == Rarity.Common) "" else rarity.name, "Rarity: ${rarity.name}", when (rarity) {
            Rarity.Common -> TagColors.Rarity.Common
            Rarity.Uncommon -> TagColors.Rarity.Uncommon
            Rarity.Rare -> TagColors.Rarity.Rare
            Rarity.Unique -> TagColors.Rarity.Unique
            else -> error("Unknown rarity $rarity")
        }
    )
}

@RFunction
fun RBuilder.SchoolTag(school: School?) {
    SpellTag(
        school?.name
            ?: "", "School: ${school?.name ?: "None"}", if (school != null) TagColors.School else Color.transparent
    )
}


fun RBuilder.actionImg(src: String, alt: String) {
    styledImg(alt, src) {
        css {
            height = 26.px
        }
    }
}

fun actionStr(actions: Int) = when (actions) {
    0 -> "Free Action"
    1 -> "1 Action"
    else -> "$actions Actions"
}

fun constantActionImg(actions: Int) = when (actions) {
    0 -> "/static/freeaction.png"
    1 -> "/static/1action.png"
    2 -> "/static/2actions.png"
    3 -> "/static/3actions.png"
    else -> error("Not a valid number of constant actions: $actions")
}

@RFunction
fun RBuilder.ActionsTag(actions: Actions) {
    styledSpan {
        css {
            spellTagStyles(Color.transparent, Color.white)
            padding(vertical = 1.px, horizontal = 0.px)
            display = Display.flex
            justifyContent = JustifyContent.center
        }

        styledSpan {
            css {
                flexShrink = 1.0
                display = Display.inlineFlex
            }
            when (actions) {
                is Actions.Constant -> {
                    actionImg(constantActionImg(actions.actions), actionStr(actions.actions))
                }
                is Actions.Variable -> {
                    actionImg(constantActionImg(actions.min), actions.min.toString())
                    styledP {
                        css {
                            margin(0.px, 7.px)
                            fontSize = LinearDimension("x-large")
                        }
                        +" to "
                    }
                    actionImg(constantActionImg(actions.max), actions.max.toString())
                }
                is Actions.Reaction -> {
                    actionImg("/static/reaction.png", "Reaction")
                }
                is Actions.Time -> {
                    actionImg("/static/time.png", "Time")
                }
            }

            if (actions.hasTrigger) {
                styledP {
                    css {
                        margin(0.px)
                        marginTop = -8.px
                        fontSize = LinearDimension("xx-large")
                    }
                    +"*"
                }
            }
        }

        attrs.title = when (actions) {
            is Actions.Constant -> if (actions.actions == 1) "1 Action" else "${actions.actions} Actions"
            is Actions.Variable -> "${actions.min} to ${actions.max} Actions"
            is Actions.Reaction -> "Reaction"
            is Actions.Time -> "Time"
        } + if (actions.hasTrigger) " , With Trigger" else ""
    }
}

@RFunction
fun RBuilder.TraitTag(trait: Trait) {
    //TODO store trait ids (lookup from traits page?)
    //link = "https://2e.aonprd.com/Traits.aspx?ID="
    SpellTag(trait.name, "Trait: ${trait.name}", TagColors.Trait)
}

@RFunction
fun RBuilder.DurationTag(rawDuration: String?, sustained: Boolean) {

    if (rawDuration == null) {
        SpellTag("Instant", "No Duration", TagColors.Duration.Instant)
    } else {
        val duration = rawDuration.trim().capitalize()

        if (sustained) {
            if (duration == "Sustained") {
                SpellTag("Duration: Sustained", TagColors.Duration.Sustained) {
                    styledImg("Sustained", "/static/sustained.png") {
                        css {
                            height = 22.px
                            verticalAlign = VerticalAlign.middle
                            margin(vertical = -2.px)
                        }
                    }
                }
            } else {
                SpellTag("Duration: $duration", TagColors.Duration.Sustained) {
                    css {
                        fontWeight = FontWeight.bold
                    }
                    val times = mutableListOf<String>()
                    val text = Regex("sustained (for )?up to ([\\w ]+)", RegexOption.IGNORE_CASE).replace(duration) {
                        times += it.groupValues[2]
                        "\$\$||\$\$"
                    }

                    val parts = text.split("\$\$")
                    parts.forEachIndexed { idx, it ->
                        if (it == "||") {
                            styledImg("Sustained", "/static/sustained.png") {
                                css {
                                    height = 22.px
                                    verticalAlign = VerticalAlign.middle

                                    if (idx != 0)
                                        marginLeft = 5.px

                                    if (idx != parts.lastIndex)
                                        marginRight = 5.px

                                    margin(vertical = -2.px)
                                }
                            }
                            +"(${times.removeFirst()})"
                        } else
                            +it
                    }
                }
            }
        } else {
            SpellTag(duration, "Duration: $duration", TagColors.Duration.NonSustained)
        }
    }
}

@RFunction
fun RBuilder.TargetingTag(targeting: TargetingType) {
    SpellTag("Targeting: $targeting.", TagColors.Targeting(targeting)) {
        if (targeting == TargetingType.Other) {
            +"Other"
        } else if (targeting == TargetingType.Area.Other) {
            +"Area"
        } else {

            val name = when (targeting) {
                TargetingType.SingleTarget -> "target"
                TargetingType.MultiTarget -> "multitarget"
                TargetingType.Area.Cone -> "cone"
                TargetingType.Area.Line -> "line"
                TargetingType.Area.Emanation -> "emanation"
                TargetingType.Area.Burst -> "burst"
                TargetingType.Area.Wall -> "wall"
                else -> error("Impossible")
            }

            styledImg(targeting.name, "/static/$name.png") {
                css {
                    height = 22.px
                    verticalAlign = VerticalAlign.middle
                    margin(vertical = -2.px)
                }
            }
        }
    }
}