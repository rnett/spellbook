package com.rnett.spellbook.model.spellbook

import com.rnett.spellbook.model.spell.SpellDefLookup
import com.rnett.spellbook.model.spell.SpellRef
import com.rnett.spellbook.utils.SerializableImmutableList
import kotlinx.serialization.Serializable

@Serializable
data class Spellcasting(
    val defaultKnownSlot: KnownSpellSlot?,
    val globalKnown: SerializableImmutableList<KnownSpellSlot>,
    val ranks: List<SpellRank>
) {
    fun known(ref: SpellRankRef): KnownSpellSlot? {
        val rank = ranks.getOrNull(ref.rank) ?: return null
        return rank.known.getOrNull(ref.index)
    }

    fun slot(ref: SpellRankRef): SpellSlot? {
        val rank = ranks.getOrNull(ref.rank) ?: return null
        return rank.slots.getOrNull(ref.index)
    }
}

@Serializable
data class SpellRankRef(val rank: Int, val index: Int) {
    override fun toString(): String {
        return "$rank[$index]"
    }
}

@Serializable
data class SpellRank(
    val known: SerializableImmutableList<KnownSpellSlot>,
    val slots: SerializableImmutableList<SpellSlot>
)

fun interface SpellcastingModifier {
    fun transform(spellcasting: Spellcasting): Spellcasting
}

@Serializable
data class SpellcastingDef(val base: Spellcasting, val modifiers: SerializableImmutableList<SpellcastingModifier>) {
    val finalSpellcasting by lazy { runCatching { modifiers.fold(base) { b, it -> it.transform(b) } } }
}

@Serializable
data class SpellcastingAndSpells(
    val spellcastingDef: SpellcastingDef,
    val slottedSpells: Map<SpellRankRef, SerializableImmutableList<SpellRef>>,
    val knownSpells: Map<SpellRankRef, SpellRef>,
    val globalKnownSpells: List<SpellRef>
) {

    val knownForRank: List<List<Pair<KnownSpellSlot, SpellRef>>> by lazy {
        val s = spellcastingDef.finalSpellcasting.getOrThrow()
        val global =
            globalKnownSpells.mapIndexed { idx, it -> (s.globalKnown.getOrNull(idx) ?: s.defaultKnownSlot!!) to it }

        return@lazy List(s.ranks.size) { rank ->
            val rank = knownSpells.filter {
                it.key.rank == rank || spellcastingDef.finalSpellcasting.getOrThrow()
                    .known(it.key)?.appliesToAllRanks == true
            }.map { (key, spell) -> s.known(key)!! to spell }
            global + rank
        }
    }

    fun validateSpells(lookup: SpellDefLookup) {
        val s = spellcastingDef.finalSpellcasting.getOrThrow()
        globalKnownSpells.forEachIndexed { idx, it ->
            val slot = s.globalKnown.getOrNull(idx) ?: s.defaultKnownSlot
            ?: throw IllegalStateException("No known spell slot for index $idx")
            if (!slot.isAllowed(lookup.getSpellDef(it))) throw IllegalStateException("Spell $it not allowed in global known slot $idx")
        }

        knownSpells.forEach { (key, spell) ->
            val slot = s.known(key) ?: throw IllegalStateException("Known slot for $key does not exist")
            if (!slot.isAllowed(lookup.getSpellDef(spell))) throw IllegalStateException("Spell $spell is not allowed in known spell slot $key")
        }

        slottedSpells.forEach { (key, spells) ->
            if (spells.isEmpty()) return@forEach

            val slot = s.slot(key) ?: throw IllegalStateException("Slot for $key does not exist")

            if (spells.size > slot.def.selectSpells) throw IllegalStateException("${spells.size} spells selected for slot $key which only supports ${slot.def.selectSpells}")

            val usableKnown =
                knownForRank[key.rank].asSequence().filter { slot.def.canUseKnown(it.first) }.map { it.second }.toSet()
            spells.forEach { spell ->
                if (!slot.def.canSelectSpell(lookup.getSpellDef(spell))) throw IllegalStateException("Spell $spell is not valid in slot $key")

                if (slot.requireKnown(spell)) {
                    if (spell !in usableKnown) throw IllegalStateException("Spell $spell is not in the known spells for $key")
                }
            }
        }
    }
}