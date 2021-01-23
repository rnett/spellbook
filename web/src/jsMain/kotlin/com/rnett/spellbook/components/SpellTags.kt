package com.rnett.spellbook.components

import com.bnorm.react.RFunction
import com.rnett.spellbook.Actions
import com.rnett.spellbook.Condition
import com.rnett.spellbook.Rarity
import com.rnett.spellbook.Save
import com.rnett.spellbook.School
import com.rnett.spellbook.TagColors
import com.rnett.spellbook.TargetingType
import com.rnett.spellbook.Trait
import com.rnett.spellbook.actionStr
import com.rnett.spellbook.asCSS
import com.rnett.spellbook.constantActionImg
import kotlinx.css.Align
import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.FlexBasis
import kotlinx.css.FlexWrap
import kotlinx.css.FontWeight
import kotlinx.css.JustifyContent
import kotlinx.css.LinearDimension
import kotlinx.css.VerticalAlign
import kotlinx.css.alignContent
import kotlinx.css.backgroundColor
import kotlinx.css.borderColor
import kotlinx.css.borderRadius
import kotlinx.css.borderWidth
import kotlinx.css.color
import kotlinx.css.display
import kotlinx.css.flexBasis
import kotlinx.css.flexGrow
import kotlinx.css.flexShrink
import kotlinx.css.flexWrap
import kotlinx.css.fontFamily
import kotlinx.css.fontSize
import kotlinx.css.fontWeight
import kotlinx.css.height
import kotlinx.css.justifyContent
import kotlinx.css.margin
import kotlinx.css.marginLeft
import kotlinx.css.marginRight
import kotlinx.css.marginTop
import kotlinx.css.padding
import kotlinx.css.px
import kotlinx.css.verticalAlign
import kotlinx.html.LI
import kotlinx.html.title
import react.RBuilder
import styled.SPANBuilder
import styled.StyleSheet
import styled.StyledDOMBuilder
import styled.css
import styled.styledImg
import styled.styledLi
import styled.styledP
import styled.styledSpan
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract



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

    if (color != Color("transparent")) {
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
fun RBuilder.SpellTag(title: String, color: Color, textColor: Color = Color("#ffffff"), builder: SPANBuilder) {
    styledSpan {
        css {
            spellTagStyles(color, textColor)
        }
        attrs.title = title
        builder()
    }
}

@RFunction
fun RBuilder.SpellTag(text: String, title: String, color: Color, textColor: Color = Color("#ffffff"), link: String? = null) {
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
        save.name + (if (basic) "" else "*"), "Save: ${if (basic) "Basic " else ""} $save", TagColors.Attack.Save(save).asCSS()
    )
}

@RFunction
fun RBuilder.AttackTag() {
    SpellTag("Attack", "Spell Attack", TagColors.Attack.Attack.asCSS())
}

@RFunction
fun RBuilder.ConditionTag(condition: Condition) {
    SpellTag(
        condition.name, "Condition: ${condition.name}", when (condition.positive) {
            true -> TagColors.Condition.Positive.asCSS()
            false -> TagColors.Condition.Negative.asCSS()
            null -> TagColors.Condition.Negative.asCSS()
        }, link = "https://2e.aonprd.com/Conditions.aspx?ID=${condition.aonId}"
    )
}

@RFunction
fun RBuilder.RarityTag(rarity: Rarity) {
    SpellTag(
        if (rarity == Rarity.Common) "" else rarity.name, "Rarity: ${rarity.name}", when (rarity) {
            Rarity.Common -> TagColors.Rarity.Common.asCSS()
            Rarity.Uncommon -> TagColors.Rarity.Uncommon.asCSS()
            Rarity.Rare -> TagColors.Rarity.Rare.asCSS()
            Rarity.Unique -> TagColors.Rarity.Unique.asCSS()
            else -> error("Unknown rarity $rarity")
        }
    )
}

@RFunction
fun RBuilder.SchoolTag(school: School?) {
    SpellTag(
        school?.name
            ?: "", "School: ${school?.name ?: "None"}", if (school != null) TagColors.School.asCSS() else Color("transparent")
    )
}


fun RBuilder.actionImg(src: String, alt: String) {
    styledImg(alt, src) {
        css {
            height = 26.px
        }
    }
}

@RFunction
fun RBuilder.ActionsTag(actions: Actions) {
    styledSpan {
        css {
            spellTagStyles(Color("transparent"), Color("#ffffff"))
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
    SpellTag(trait.name, "Trait: ${trait.name}", TagColors.Trait.asCSS())
}

@RFunction
fun RBuilder.DurationTag(rawDuration: String?, sustained: Boolean) {

    if (rawDuration == null) {
        SpellTag("Instant", "No Duration", TagColors.Duration.Instant.asCSS())
    } else {
        val duration = rawDuration.trim().capitalize()

        if (sustained) {
            if (duration == "Sustained") {
                SpellTag("Duration: Sustained", TagColors.Duration.Sustained.asCSS()) {
                    styledImg("Sustained", "/static/sustained.png") {
                        css {
                            height = 22.px
                            verticalAlign = VerticalAlign.middle
                            margin(vertical = -2.px)
                        }
                    }
                }
            } else {
                SpellTag("Duration: $duration", TagColors.Duration.Sustained.asCSS()) {
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
            SpellTag(duration, "Duration: $duration", TagColors.Duration.NonSustained.asCSS())
        }
    }
}

@RFunction
fun RBuilder.TargetingTag(targeting: TargetingType) {
    SpellTag("Targeting: $targeting.", TagColors.Targeting(targeting).asCSS()) {
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