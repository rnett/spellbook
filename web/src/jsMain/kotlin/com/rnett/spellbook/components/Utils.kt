package com.rnett.spellbook.components

import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.JustifyContent
import kotlinx.css.StyledElement
import kotlinx.css.color
import kotlinx.css.hyphenize
import kotlinx.css.properties.TextDecoration
import kotlinx.css.textDecoration
import kotlinx.html.title
import react.RBuilder
import styled.css
import styled.styledA
import kotlin.reflect.KProperty

fun RBuilder.builtinA(link: String, title: String? = null, newTab: Boolean = true, style: CSSBuilder.() -> Unit = {}, block: RBuilder.() -> Unit) {
    styledA {
        css {
            textDecoration = TextDecoration.none
            color = Color.inherit
            style()
        }
        attrs.href = link

        if (newTab)
            attrs.target = "_blank"

        if (title != null)
            attrs.title = title

        block()
    }
}

class CustomCSSProperty<T>(val name: String? = null, private val default: (() -> T)? = null) {
    operator fun getValue(thisRef: StyledElement, property: KProperty<*>): T {
        default?.let { default ->
            if (!thisRef.declarations.containsKey(name ?: property.name)) {
                thisRef.declarations[name ?: property.name] = default() as Any
            }
        }

        @Suppress("UNCHECKED_CAST")
        return thisRef.declarations[name ?: property.name] as T
    }

    operator fun setValue(thisRef: StyledElement, property: KProperty<*>, value: T) {
        thisRef.declarations[name ?: property.name] = value as Any
    }
}

fun <T> cssProperty(name: String? = null, default: (() -> T)? = null) = CustomCSSProperty(name, default)


class WrappedCSSVariable<T>(val name: String? = null, val wrap: (String) -> T) {
    operator fun getValue(thisRef: StyledElement, property: KProperty<*>): T {
        return wrap("var(--${(name ?: property.name).hyphenize()})")
    }

    operator fun setValue(thisRef: StyledElement, property: KProperty<*>, value: T) {
        thisRef.declarations["--${name ?: property.name}"] = value as Any
    }
}

fun <T> cssVariable(wrap: (String) -> T) = WrappedCSSVariable(null, wrap)

fun <T> cssVariable(name: String, wrap: (String) -> T) = WrappedCSSVariable(name, wrap)

var CSSBuilder.justifySelf: JustifyContent by CustomCSSProperty()