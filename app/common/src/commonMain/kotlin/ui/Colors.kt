package com.rnett.spellbook.ui

import androidx.compose.ui.graphics.Color
import com.rnett.spellbook.model.spell.*
import com.rnett.spellbook.spell.*
import com.rnett.spellbook.ui.TagColors.Area.AreaType

private const val TRANSPARENT = "transparent"

fun Color(hexString: String, alpha: Float = 1f): Color {
    if (hexString.isBlank() || hexString.lowercase() == TRANSPARENT)
        return Color.Transparent
    return Color(
        ("FF" + hexString.trim('#')).lowercase().toLong(16)
    )
        .copy(alpha = alpha)
}

object MainColors {
    val outsideColor = Color("#424242")
    val borderColor = Color("#563B3B")
    val spellBorderColor = Color("#563B3B")
    val spellBodyColor = Color("#634a45")
    val textColor = Color("#ffffff")
    val infoBoxColor = Color("#696969")
    val infoHeaderColor = Color("#522e2c")
    val tooltipColor = Color("#74705A")
}

object SavedSearchColors {
    val searchButtonColor = Color("#3153AF")
}

object FilterColors {
    val dividerColor = Color("#AAAAAA")
    val adderSpaceColor = Color("#555454")
    val typeButtonColor = Color("#6C6C6C")
    val checkboxRequired = Color("#496E4C")
    val checkboxForbidden = Color("#7B3D3C")
    val cancelReset = Color("#FF0000", 0.4f)
    val goToFullPage = Color("#4D5E76")
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
            com.rnett.spellbook.model.spell.Rarity.Common -> Common
            com.rnett.spellbook.model.spell.Rarity.Uncommon -> Uncommon
            com.rnett.spellbook.model.spell.Rarity.Rare -> Rare
            com.rnett.spellbook.model.spell.Rarity.Unique -> Unique
            else -> error("Unknown rarity $rarity")
        }
    }

    val School = Color("#9400d3", 0.4f)

    fun SpellList(spellList: SpellList): Color = when (spellList) {
        SpellList.Arcane -> Color("#4169e1")
        SpellList.Divine -> Color("#ffd700")
        SpellList.Occult -> Color("#c0c0c0")
        SpellList.Primal -> Color("#008000")
        SpellList.Focus -> Color("#6b8e23")
        SpellList.Other -> Color("#696969")
        SpellList.Elemental -> Color("#b57b10")
    }

    val Trait = Color("#a52a2a", 0.4f)
    val Incapacitation = Color("#AA1600", 0.75f)

    object Duration {
        val Sustained = Color("#08AE58")
        val NonSustained = Color("#ff8c00")
        val Instant = Color("#483d8b")
    }

    object Area {
        fun AreaType(areaType: AreaType): Color = when (areaType) {
            AreaType.Burst -> Color("#ff0000")
            AreaType.Line -> Color("#5EB4C6")
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
        TargetingType.Area.Line -> Color("#5EB4C6")
        TargetingType.Area.Emanation -> Color("#B8A025")
        TargetingType.Area.Burst -> Color("#ff0000")
        TargetingType.Area.Wall -> Color("#2f4f4f")
        TargetingType.Area.Other -> Color("#8F5C5C")
    }

    fun SpellType(type: SpellType): Color = when (type) {
//        SpellType.Cantrip -> Color("#7C9BA9")
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