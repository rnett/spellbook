package com.rnett.spellbook.spell

import kotlinx.serialization.Serializable

@Serializable
enum class AreaType {
    Burst, Line, Emanation, Cone, Wall;
}

@Serializable
sealed class SpellArea {
    abstract val text: String

    abstract fun hasType(type: AreaType): Boolean

    @Serializable
    data class Untyped(override val text: String) : SpellArea() {
        override fun hasType(type: AreaType) = false
    }

    @Serializable
    data class PartiallyTyped(override val text: String, val type: AreaType) : SpellArea() {
        override fun hasType(type: AreaType) = this.type == type
    }

    @Serializable
    data class Typed(override val text: String, val size: Int, val type: AreaType) : SpellArea() {
        override fun hasType(type: AreaType): Boolean = this.type == type
    }

    @Serializable
    data class Multiple(override val text: String, val parts: List<SpellArea>) : SpellArea() {
        override fun hasType(type: AreaType): Boolean = parts.any { it.hasType(type) }
    }

    companion object {

        private val typedRegex =
            Regex(
                "(\\d+)-foot(?:-radius) (${AreaType.values().joinToString("|") { it.name.lowercase() }})",
                RegexOption.IGNORE_CASE
            )

        operator fun invoke(text: String): SpellArea = if ("," in text) {
            val parts = text.split(",").map { it.trim() }

            doMultiple(text, parts.dropLast(1) + parts.last().removePrefix("or").trim())
        } else {

            if (" or " in text) {
                val parts = text.split(" or ").map { it.trim() }
                doMultiple(text, parts)
            } else {
                val match = typedRegex.matchEntire(text)

                if (match != null) {
                    Typed(text, match.groupValues[1].toInt(), AreaType.valueOf(match.groupValues[2].capitalize()))
                } else
                    tryPartialType(text)
            }
        }

        private fun doMultiple(text: String, parts: List<String>): SpellArea {
            val areas = parts.map { SpellArea(it) }

            return if (areas.all { it !is Untyped })
                Multiple(text, areas)
            else
                tryPartialType(text)
        }

        private fun tryPartialType(text: String): SpellArea {
            val partial = AreaType.values().singleOrNull { it.name.lowercase() in text.lowercase() }

            return if (partial != null) {
                PartiallyTyped(text, partial)
            } else {
                Untyped(text)
            }
        }

    }
}