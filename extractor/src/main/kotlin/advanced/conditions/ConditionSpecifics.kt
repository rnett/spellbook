package com.rnett.spellbook.extractor.advanced.conditions

import com.rnett.spellbook.extractor.basic.Check
import kotlinx.serialization.Serializable


/**
 * A stat or check affected by a condition
 *
 * @property penalty Whether the effect is a penalty (false if it is a bonus)
 * @property type The type of bonus or penalty, i.e. status or circumstance.
 * @property stat The stat affected (i.e. attack, Fortitude, Perception, Intimidation).
 * @property check If the stat affected is a check (i.e. Fortitude, attack, Reflex), the check.
 */
@Serializable
data class AffectedStat(val penalty: Boolean, val type: String?, val stat: String, val check: Check?)