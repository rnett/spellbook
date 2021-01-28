package com.rnett.spellbook.components

import com.bnorm.react.RFunction
import com.bnorm.react.RKey
import com.rnett.spellbook.Spell
import com.rnett.spellbook.TagColors
import com.rnett.spellbook.asCSS
import com.rnett.spellbook.debugColor
import com.rnett.spellbook.spellBodyColor
import com.rnett.spellbook.spellBorderColor
import com.rnett.spellbook.uninterestingConditions
import kotlinx.css.BorderStyle
import kotlinx.css.Color
import kotlinx.css.FontWeight
import kotlinx.css.JustifyContent
import kotlinx.css.backgroundColor
import kotlinx.css.borderColor
import kotlinx.css.borderStyle
import kotlinx.css.borderWidth
import kotlinx.css.fontWeight
import kotlinx.css.margin
import kotlinx.css.marginLeft
import kotlinx.css.padding
import kotlinx.css.paddingTop
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.html.title
import react.RBuilder
import styled.css
import styled.styledHr
import styled.styledP

@RFunction
fun RBuilder.HeaderGrid(style: StyleBuilder = {}, rows: ElementBuilder) =
    Grid({ backgroundColor = spellBorderColor; style() }, rows)

@RFunction
fun RBuilder.HeaderRow(
    justifyContent: JustifyContent = JustifyContent.spaceBetween,
    style: StyleBuilder = {},
    cols: ElementBuilder
) = Row(justifyContent, {
    padding(10.px)
//    paddingTop = 2.5.px
//    paddingBottom = 8.px
    style()
}, cols)

@RFunction
fun RBuilder.SpellHeader(spell: Spell, @RKey key: String = spell.name) {

    HeaderGrid {
        HeaderRow {
            css {
                paddingTop = 5.px
            }
            Col(20.pct, JustifyContent.flexStart) {
                css {
                    justifySelf = JustifyContent.flexStart
                    debugColor = Color("#ff0000")
                }

                builtinA("https://2e.aonprd.com/Spells.aspx?ID=${spell.aonId}", style = {
                    fontWeight = FontWeight.bold
                    paddingTop = 3.px
                }) {
                    +spell.name
                }
            }
            Col(10.pct, JustifyContent.flexEnd) {
                css {
                    justifySelf = JustifyContent.flexEnd
                    declarations["order"] = 100
                    debugColor = Color.pink
                }
                styledP {
                    css {
                        fontWeight = FontWeight.bold
                        paddingTop = 3.px
                    }
                    +"${spell.type} ${spell.level}"
                }
            }

            Col(20.pct, JustifyContent.center) {
                css {
                    justifySelf = JustifyContent.flexStart
                    debugColor = Color.purple
                    marginLeft = 10.px
                }

                ActionsTag(spell.actions)
            }

            Col(30.pct) {
                css {
                    justifySelf = JustifyContent.flexStart
                    debugColor = Color.skyBlue
                    attrs.title = "Spell Lists: ${spell.lists.joinToString(", ")}"
                    marginLeft = 10.px
                }

                spell.lists.sorted().forEach {
                    SpellTag(it.name, "Spell List: $it", TagColors.SpellList(it).asCSS())

                }
            }

            Col(20.pct, JustifyContent.center) {
                css {
                    justifySelf = JustifyContent.flexStart
                    debugColor = Color("#ffd700")
                    marginLeft = 10.px
                }
                SchoolTag(spell.school)
            }

            Col(30.pct, grow = 0.5) {
                css {
                    debugColor = Color("#ff0000")
                }
                if (spell.requiresAttackRoll)
                    AttackTag()

                if (spell.save != null)
                    SaveTag(spell.save!!, spell.basicSave)
            }

            Col(grow = 0.5) {
                css {
                    debugColor = Color("#4169e1")
                }
                spell.conditions.forEach {
                    if (it.name !in uninterestingConditions)
                        ConditionTag(it)
                }
            }

        }
        fun hBar() {
            styledHr {
                css {
                    margin(0.px)
                    borderColor = spellBodyColor
                    borderStyle = BorderStyle.solid
                    borderWidth = 1.px
                }
            }
        }

        hBar()

        HeaderRow {

            Col(20.pct, grow = 0) {
                css {
                    debugColor = Color.yellowGreen
                }
                DurationTag(spell.duration, spell.sustained)
            }

            Col(20.pct, grow = 0.5) {
                css {
                    debugColor = Color.yellowGreen
                }
                spell.targeting?.let { it.forEach { TargetingTag(it) } }
            }
        }

        hBar()

        HeaderRow {

            Col(12.8.pct, JustifyContent.center) {
                css {
                    justifySelf = JustifyContent.flexStart
                    debugColor = Color.blue
                }
                RarityTag(spell.rarity)
            }
            Col(justifyContent = JustifyContent.flexStart) {
                css {
                    justifySelf = JustifyContent.flexStart
                    debugColor = Color("#008000")
                    marginLeft = -20.px
                    "> *"{
                        marginLeft = 20.px
                    }
                }
                spell.traits.forEach {
                    if (it.isInteresting)
                        TraitTag(it)
                }
            }
        }
    }

//    styledDiv {
//        css{
//            display = Display.flex
//            flexDirection = FlexDirection.column
//            backgroundColor = spellBorderColor
//        }
//
//        fun CSSBuilder.rowStyle(){
//            display = Display.flex
//            flexDirection = FlexDirection.row
//            justifyContent = JustifyContent.spaceBetween
//            padding(horizontal = 10.px)
//            paddingTop = 2.5.px
//            paddingBottom = 8.px
//            alignContent = Align.flexStart
//            flexGrow = 1.0
//        }
//
//        styledDiv {
//            css {
//                rowStyle()
//            }
//
//            // name
//            styledDiv {
//                css {
//                    display = Display.flex
//                    width = 8.rem
//                    flexWrap = FlexWrap.wrap
//
//                }
//                styledDiv {
//                    css {
//                        flexShrink = 1.0
//                        justifyContent = JustifyContent.center
//                    }
//
//                    builtinA("https://2e.aonprd.com/Spells.aspx?ID=${spell.aonId}", style = {
//                        fontWeight = FontWeight.bold
//                        paddingTop = 3.px
//                    }) {
//                        +spell.name
//                    }
//                }
//            }
//
//            styledSpan {
//                css {
//                    declarations["order"] = 100
//                    fontWeight = FontWeight.bold
//                    paddingTop = 3.px
//                    width = 5.rem
//                    textAlign = TextAlign.right
//                }
//                +"${spell.type} ${spell.level}"
//            }
//
//            // tags
//
//            styledDiv {
//                css {
//                    marginTop = -10.px
//                    display = Display.flex
//                    alignItems = Align.baseline
//                }
//                ActionsTag(spell.actions)
//            }
//
//            styledDiv {
//                css {
//                    display = Display.flex
//                    width = 250.px
//                    marginRight = 20.px
//                }
//                SpellListsTags(spell.lists)
//            }
//
//            styledDiv {
//                css {
//                    marginTop = -10.px
//                    display = Display.flex
//                    alignItems = Align.baseline
//                    justifyContent = JustifyContent.center
//                    width = 130.23.px
//                }
//                SchoolTag(spell.school)
//            }
//
//            styledUl {
//                css {
//                    display = Display.flex
//                    flexGrow = 1.0
//                    margin(0.px)
//                    padding(0.px)
//                }
//
//                SpacingTagGroup {
//                    if (spell.requiresAttackRoll)
//                        AttackTag()
//
//                    if (spell.save != null)
//                        SaveTag(spell.save, spell.basicSave)
//                }
//
//                SpacingTagGroup {
//                    spell.conditions.forEach {
//                        if (it.name !in uninterestingConditions)
//                            ConditionTag(it)
//                    }
//                }
//            }
//        }
//
//        styledHr {
//            css{
//                margin(0.px)
//                borderColor = BaseStyles.backgroundColor
//            }
//        }
//
//        styledDiv {
//            css{
//                rowStyle()
//                marginTop = 10.px
//                justifyContent = JustifyContent.flexStart
//            }
//
//            CompactTagGroup({ css{ width = 110.px ; marginRight = 20.px } }) {
//                RarityTag(spell.rarity)
//            }
//
//            CompactTagGroup( {
//                css{
//                    justifyContent = JustifyContent.flexStart
//                    marginLeft = -20.px
//
//                    "> span"{
//                        marginLeft = 20.px
//                    }
//                }
//            }) {
//                spell.traits.forEach {
//                    if (it.name !in uninterestingConditions)
//                        TraitTag(it)
//                }
//            }
//        }
//    }
}