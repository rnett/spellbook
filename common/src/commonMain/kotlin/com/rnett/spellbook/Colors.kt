package com.rnett.spellbook

data class Color(val hexString: String) {
    companion object {
        val Transparent = Color("transparent")
    }
}

object MainColors{
    val outsideColor = Color("#424242")
    val spellBorderColor = Color("#563B3B")
    val spellBodyColor = Color("#634a45")
    val textColor = Color("#ffffff")
    val infoBoxColor = Color("#696969")
    val infoHeaderColor = Color("#522e2c")
}

object TagColors {
    object Attack {
        fun Save(save: Save) = when(save){
            Save.Fortitude -> Color("#008000")
            Save.Reflex -> Color("#4682b4")
            Save.Will -> Color("#ffd700")
        }

        val Attack = Color("#dc143c")
    }

    object Condition {
        val Positive = Color("#2e8b57")
        val Negative = Color("#cd5c5c")
        val Neutral = Color("#a9a9a9")
    }

    object Rarity {
        val Unique = Color("#0c1466")
        val Rare = Color("#0c1466")
        val Uncommon = Color("#c45500")
        val Common = Color("transparent")

        operator fun invoke(rarity: Trait) = when (rarity.key) {
            com.rnett.spellbook.Rarity.Common -> Common
            com.rnett.spellbook.Rarity.Uncommon -> Uncommon
            com.rnett.spellbook.Rarity.Rare -> Rare
            com.rnett.spellbook.Rarity.Unique -> Unique
            else -> error("Unknown rarity $rarity")
        }
    }

    val School = Color("#9400d3")

    fun SpellList(spellList: SpellList): Color = when (spellList) {
        SpellList.Arcane -> Color("#4169e1")
        SpellList.Divine -> Color("#ffd700")
        SpellList.Occult -> Color("#c0c0c0")
        SpellList.Primal -> Color("#008000")
        SpellList.Focus -> Color("#6b8e23")
        SpellList.Other -> Color("#696969")
    }

    val Trait = Color("#a52a2a")

    object Duration {
        val Sustained = Color("#08AE58")
        val NonSustained = Color("#ff8c00")
        val Instant = Color("#483d8b")
    }

    object Area {
        fun AreaType(areaType: AreaType): Color = when (areaType) {
            AreaType.Burst -> Color("#ff0000")
            AreaType.Line -> Color("#87cefa")
            AreaType.Emanation -> Color("#FF5733")
            AreaType.Cone -> Color("#bdb76b")
            AreaType.Wall -> Color("#2f4f4f")
        }

        val Untyped = Color("#BB7D70")
    }

    fun Targeting(targeting: TargetingType): Color = when (targeting) {
        TargetingType.SingleTarget -> Color("#900C3F")
        TargetingType.MultiTarget -> Color("#9e4a1c")
        TargetingType.Other -> Color("#a9a9a9")
        TargetingType.Area.Cone -> Color("#448BB3")
        TargetingType.Area.Line -> Color("#3BE8EE")
        TargetingType.Area.Emanation -> Color("#B8A025")
        TargetingType.Area.Burst -> Color("#ff0000")
        TargetingType.Area.Wall -> Color("#2f4f4f")
        TargetingType.Area.Other -> Color("#8F5C5C")
    }
}