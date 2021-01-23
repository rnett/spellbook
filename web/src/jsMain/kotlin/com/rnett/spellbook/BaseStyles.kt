package com.rnett.spellbook

import com.rnett.spellbook.components.cssVariable
import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.backgroundColor
import kotlinx.css.color
import kotlinx.css.fontFamily
import kotlinx.css.minHeight
import kotlinx.css.pct
import styled.StyleSheet

var CSSBuilder.outsideColor by cssVariable(::Color)
    private set

var CSSBuilder.spellBorderColor by cssVariable(::Color)
    private set

var CSSBuilder.spellBodyColor by cssVariable(::Color)
    private set

var CSSBuilder.textColor by cssVariable(::Color)
    private set

private var CSSBuilder._debugColor by cssVariable("debugColor", ::Color)

var CSSBuilder.font by cssVariable({ it })
    private set

var CSSBuilder.debugColor
    get() = _debugColor
    set(value) {
        _debugColor = value
        classes.add("debug")
    }

var debugByDefault = false

object BaseStyles : StyleSheet("Base") {
    val global by css {
        outsideColor = MainColors.outsideColor.asCSS()
        spellBorderColor = MainColors.spellBodyColor.asCSS()
        spellBodyColor = MainColors.spellBodyColor.asCSS()
        textColor = MainColors.textColor.asCSS()
        font = "Helvetica"

        ".debug"{
            backgroundColor = if (debugByDefault)
                _debugColor
            else
                Color.inherit
        }
    }

    val page by css {
        fontFamily = font
        color = textColor
        backgroundColor = outsideColor
        minHeight = 100.pct
    }
}