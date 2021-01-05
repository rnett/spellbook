package com.rnett.spellbook.components

import com.bnorm.react.RFunction
import com.bnorm.react.RKey
import com.rnett.spellbook.Spell
import com.rnett.spellbook.spellBodyColor
import com.rnett.spellbook.spellBorderColor
import kotlinext.js.js
import kotlinx.css.*
import kotlinx.css.properties.border
import kotlinx.html.HR
import kotlinx.html.P
import react.RBuilder
import react.dom.b
import styled.*

@RFunction
fun RBuilder.SpellDisplay(spell: Spell, @RKey key: String = spell.name) {
    styledDiv {
        css {
            borderRadius = 15.px
            marginBottom = 20.px
            val borderSize = 5.px
            border(borderSize, BorderStyle.solid, spellBorderColor)
        }

        SpellHeader(spell)

        styledDiv {
            css {
                padding(10.px)
                backgroundColor = spellBodyColor
            }

            fun item(key: String, value: String?, builder: StyledDOMBuilder<P>.() -> Unit = {}) {
                if (value != null) {

                    styledP {
                        css {
                            margin(0.px)
                        }
                        builder()
                        b { +"${key.capitalize()}: " }
                        +value.capitalize()
                    }
                }
            }

            fun split(builder: StyledDOMBuilder<HR>.() -> Unit = {}) {
                styledHr {
                    css {
                        margin(horizontal = -20.px, vertical = 3.px)
                        borderColor = spellBorderColor
                        borderStyle = BorderStyle.solid
                        borderWidth = 1.px
                    }
                    builder()
                }
            }

            item("Spoilers", spell.spoilersFor)
            item("From", spell.source)

            split()

            item("Cast Actions", spell.actionTypes?.joinToString(", "))
            item("Requirements", spell.requirements)
            item("Trigger", spell.actions.trigger)

            split()

            item("Range", spell.range)
            item("Targets", spell.targets)
            item("Area", spell.area)
            item("Duration", spell.duration)

            split() {
                css {
                    marginBottom = 10.px
                }
            }

            styledP {
                css {
                    margin(0.px)
                    padding(vertical = 10.px)
                }
                attrs["dangerouslySetInnerHTML"] = js {
                    this["__html"] = spell.description
                } as Any
            }

            //TODO heightening

            //TODO summons

            //TODO postfix

        }
    }
}
