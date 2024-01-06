package com.rnett.spellbook.extractor

import kotlinx.serialization.Serializable

/**
 * Metadata about how the spell was extracted or converted by you.
 *
 * @property fullyCaptured Whether the spell has been fully captured in the JSON.  False if there are parts of the spell or its effects that are unable to be represented here.
 * @property caveats A description about any caveats you have about the spell's representation in JSON.
 * @property confidence Your confidence that the spell's JSON is fully correct.  Should be a number from 1 (low confidence) to 10 (high confidence).
 */
@Serializable
data class ExtractionMetadata(
    val fullyCaptured: Boolean,
    val caveats: String,
    val confidence: Int
)