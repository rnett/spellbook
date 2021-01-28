package com.rnett.spellbook

import kotlinx.serialization.Serializable
import kotlin.properties.PropertyDelegateProvider

@Serializable
data class TraitKey(val name: String) {
    infix fun eq(trait: Trait) = trait.name == name
}

infix fun Trait.eq(key: TraitKey) = key eq this

abstract class TraitFamily(val familyName: String) {
    private val _definedTraits = mutableSetOf<TraitKey>()

    val traits get() = _definedTraits.toSet()

    protected fun trait() = PropertyDelegateProvider<Any?, Lazy<TraitKey>> { _, prop ->
        val key = TraitKey(prop.name).also { _definedTraits += it }
        lazy { key }
    }

    protected fun trait(name: String) = PropertyDelegateProvider<Any?, Lazy<TraitKey>> { _, _ ->
        val key = TraitKey(name).also { _definedTraits += it }
        lazy { key }
    }

    operator fun contains(trait: Trait) = trait.key in _definedTraits

    operator fun contains(trait: TraitKey) = trait in _definedTraits

    infix fun assertIn(trait: Trait): Trait = trait.also {
        require(trait in this) { "Trait $trait is not in $familyName trait family" }
    }
}

infix fun Trait.assertIn(traitFamily: TraitFamily) = traitFamily assertIn this

@Serializable
data class Trait(val name: String, val aonId: Int, val description: String) {

    companion object {
        val Attack = TraitKey("Attack")
    }

    val isInteresting by lazy { name !in uninterestingConditions && this !in School && this !in Rarity }

    val key by lazy { TraitKey(name) }
}

object School : TraitFamily("School") {
    val Abjuration by trait()

    val Conjuration by trait()

    val Divination by trait()

    val Enchantment by trait()

    val Evocation by trait()

    val Illusion by trait()

    val Necromancy by trait()

    val Transmutation by trait()

    val schools get() = traits
}

object Rarity : TraitFamily("Rarity") {
    val Common by trait()
    val Uncommon by trait()
    val Rare by trait()
    val Unique by trait()

    val rarities get() = traits
}