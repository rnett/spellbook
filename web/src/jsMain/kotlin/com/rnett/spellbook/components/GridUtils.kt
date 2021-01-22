package com.rnett.spellbook.components

import com.bnorm.react.RFunction
import kotlinx.css.Align
import kotlinx.css.CSSBuilder
import kotlinx.css.Display
import kotlinx.css.FlexBasis
import kotlinx.css.FlexDirection
import kotlinx.css.FlexWrap
import kotlinx.css.JustifyContent
import kotlinx.css.LinearDimension
import kotlinx.css.alignContent
import kotlinx.css.display
import kotlinx.css.flexBasis
import kotlinx.css.flexDirection
import kotlinx.css.flexGrow
import kotlinx.css.flexWrap
import kotlinx.css.justifyContent
import kotlinx.css.margin
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.html.DIV
import react.RBuilder
import styled.StyledDOMBuilder
import styled.css
import styled.styledDiv


typealias ElementBuilder = StyledDOMBuilder<DIV>.() -> Unit
typealias StyleBuilder = CSSBuilder.() -> Unit


@RFunction
fun RBuilder.Grid(style: StyleBuilder = {}, rows: ElementBuilder) {
    styledDiv {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            style()
        }
        rows()
    }
}

@RFunction
fun RBuilder.Row(
    justifyContent: JustifyContent = JustifyContent.spaceBetween,
    style: StyleBuilder = {},
    cols: ElementBuilder
) {
    styledDiv {
        css {
            display = Display.flex
            flexDirection = FlexDirection.row
            this.justifyContent = justifyContent
            alignContent = Align.flexStart
            flexGrow = 1.0
            style()
        }
        cols()
    }
}

@RFunction
fun RBuilder.Col(
    basis: LinearDimension = 100.pct,
    justifyContent: JustifyContent = JustifyContent.spaceEvenly,
    grow: Number = 0,
    vertSpacing: LinearDimension = 10.px,
    horizSpacing: LinearDimension = 20.px,
    style: StyleBuilder = {},
    elements: ElementBuilder
) = Col(basis.value, justifyContent, grow, vertSpacing, horizSpacing, style, elements)

@RFunction
fun RBuilder.Col(
    basis: String,
    justifyContent: JustifyContent = JustifyContent.spaceEvenly,
    grow: Number = 0,
    vertSpacing: LinearDimension = 10.px,
    horizSpacing: LinearDimension = 20.px,
    style: StyleBuilder = {},
    elements: ElementBuilder
) {
    styledDiv {
        css {
            display = Display.flex
            flexGrow = grow.toDouble()
            flexBasis = FlexBasis(basis)
            flexWrap = FlexWrap.wrap
            alignContent = Align.baseline
            this.justifyContent = justifyContent

            margin(-vertSpacing, -horizSpacing / 2)

            "> *"{
                margin(vertSpacing, horizSpacing / 2)
            }

            style()
        }
        elements()
    }
}

