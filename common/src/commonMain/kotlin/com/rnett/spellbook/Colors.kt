package com.rnett.spellbook

private const val TRANSPARENT = "transparent"

class Color(private val _hexString: String, val alpha: Float = 1f) {
    val isTransparent by lazy { _hexString.isBlank() || _hexString.toLowerCase() == TRANSPARENT }

    val hexString by lazy {
        if (!_hexString.startsWith("#") && !isTransparent)
            "#" + _hexString.toLowerCase()
        else
            _hexString
    }

    companion object {
        val Transparent = Color(TRANSPARENT, 0f)
    }

    fun withAlpha(alpha: Float): Color = if (hexString != "transparent")
        Color(hexString, alpha)
    else
        Color.Transparent

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Color

        if (hexString != other.hexString) return false
        if (alpha != other.alpha) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hexString.hashCode()
        result = 31 * result + alpha.hashCode()
        return result
    }
}

object MainColors {
    val outsideColor = Color("#424242")
    val spellBorderColor = Color("#563B3B")
    val spellBodyColor = Color("#634a45")
    val textColor = Color("#ffffff")
    val infoBoxColor = Color("#696969")
    val infoHeaderColor = Color("#522e2c")
}

object SavedSearchColors {
    val searchButtonColor = Color("#3153AF")
}

object FilterColors {
    val dividerColor = Color("#AAAAAA")
    val adderSpaceColor = Color("#555454")
    val typeButtonColor = Color("#6C6C6C")
    val checkboxRequired = Color("#445945")
    val checkboxForbidden = Color("#6C575C")
}

object TagColors {
    object Attack {
        fun Save(save: Save) = when (save) {
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

    val School = Color("#9400d3", 0.6f)

    fun SpellList(spellList: SpellList): Color = when (spellList) {
        SpellList.Arcane -> Color("#4169e1")
        SpellList.Divine -> Color("#ffd700")
        SpellList.Occult -> Color("#c0c0c0")
        SpellList.Primal -> Color("#008000")
        SpellList.Focus -> Color("#6b8e23")
        SpellList.Other -> Color("#696969")
    }

    val Trait = Color("#a52a2a", 0.6f)

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

    fun SpellType(type: SpellType): Color = when (type) {
        SpellType.Cantrip -> Color("#7C9BA9")
        SpellType.Spell -> Color("#A9709C")
        SpellType.Focus -> SpellList(SpellList.Focus)
    }

    fun ActionType(type: CastActionType): Color = when (type) {
        CastActionType.Material -> Color("#797926")
        CastActionType.Somatic -> Color("#604B6C")
        CastActionType.Verbal -> Color("#476C59")
        CastActionType.Focus -> SpellList(SpellList.Focus)
    }
}