package com.rnett.spellbook

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("BaseTrait")
sealed class Trait() {
    abstract val name: String

    @Serializable
    @SerialName("Attack")
    object Attack : Trait() {
        override val name: String = "Attack"
    }

    @Serializable
    @SerialName("Trait")
    class Other
    @Deprecated("Use Companion.invoke on JVM", level = DeprecationLevel.WARNING)
    internal constructor(override val name: String) : Trait()

    companion object {
        val definedTraits by lazy { (School.schools + Rarity.rarities + Attack).associateBy { it.name } }
    }

    override fun toString(): String {
        return "Trait($name)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Trait) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}

@Serializable
@SerialName("School")
class School private constructor(override val name: String) : Trait() {

    companion object {

        val Abjuration = School("Abjuration")

        val Conjuration = School("Conjuration")

        val Divination = School("Divination")

        val Enchantment = School("Enchantment")

        val Evocation = School("Evocation")

        val Illusion = School("Illusion")

        val Necromancy = School("Necromancy")

        val Transmutation = School("Transmutation")

        val schools = setOf(Abjuration, Conjuration, Divination, Enchantment, Evocation, Illusion, Necromancy, Transmutation)
    }

    override fun toString(): String {
        return "School($name)"
    }
}

@Serializable
@SerialName("Rarity")
class Rarity private constructor(override val name: String, val index: Int) : Trait() {

    operator fun compareTo(other: Rarity) = index - other.index

    companion object {

        val Common = Rarity("Common", 0)

        val Uncommon = Rarity("Uncommon", 1)

        val Rare = Rarity("Rare", 2)

        val Unique = Rarity("Unique", 3)

        val rarities = setOf(Common, Uncommon, Rare, Unique)
    }

    override fun toString(): String {
        return "Rarity($name)"
    }
}