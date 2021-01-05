package com.rnett.spellbook.components

import com.bnorm.react.RFunction
import com.rnett.spellbook.BaseStyles
import com.rnett.spellbook.Spell
import com.rnett.spellbook.SpellType
import com.rnett.spellbook.filter.Filter
import com.rnett.spellbook.filter.LevelFilter
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.krosstalk.getSpells
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.css.padding
import kotlinx.css.px
import react.*
import styled.css
import styled.styledDiv

@RFunction
fun RBuilder.SpellPage() {
    styledDiv {
        css {
            padding(20.px)
            +BaseStyles.page
        }

        var spells by useState<Set<Spell>>(emptySet())

        useEffect(emptyList()) {
            GlobalScope.launch {
                spells = getSpells(
                    SpellFilter(
                        level = LevelFilter(3),
//                        lists = Filter.Or(SpellList.Focus, SpellList.Arcane),
                        types = Filter.Or(
                            SpellType.Spell, SpellType.Focus
                        )
                    )
                )
            }
        }

        spells.forEach {
            SpellDisplay(it)
        }
    }
}