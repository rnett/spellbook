package com.rnett.spellbook.components

import com.bnorm.react.RFunction
import com.rnett.spellbook.Spell
import com.rnett.spellbook.SpellList
import com.rnett.spellbook.filter.AttackType
import com.rnett.spellbook.filter.Filter
import com.rnett.spellbook.filter.LevelFilter
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.krosstalk.getSpells
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.css.br
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.br
import styled.css
import styled.styledDiv
import styled.styledInput


@RFunction
fun RBuilder.Welcome(initialName: String) {
    var name by useState(initialName)
    var spells by useState<Set<Spell>>(emptySet())

    useEffect(emptyList()) {
        GlobalScope.launch {
            spells = getSpells(SpellFilter(level = LevelFilter(6), lists = Filter.Or(SpellList.Arcane)))
        }
    }

    +"Spells"
    br {}
    spells.forEach {
        SpellDisplay(it)
    }
}
