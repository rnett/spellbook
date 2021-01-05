package com.rnett.spellbook

import com.rnett.spellbook.components.SpellPage
import kotlinx.browser.document
import kotlinx.browser.window
import react.dom.render
import styled.injectGlobal

fun main() {
    window.onload = {
        document.body?.style?.margin = "0"
        render(document.getElementById("root")) {
            injectGlobal {
                ":root"{
                    +BaseStyles.global
                }
            }
            SpellPage()
        }
    }
}
