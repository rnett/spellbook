package com.rnett.spellbook

import com.rnett.spellbook.components.Welcome
import com.rnett.spellbook.filter.AttackType
import com.rnett.spellbook.krosstalk.MyKrosstalk
import react.dom.render
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import styled.injectGlobal

fun main() {
    window.onload = {
        render(document.getElementById("root")) {
            Welcome("Kotlin/JS")
        }
    }
}
