package com.rnett.spellbook.ui.components

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

@Immutable
data object PlaceholderTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText =
        if (text.isEmpty())
            TransformedText(AnnotatedString(" "), Offset)
        else
            TransformedText(text, OffsetMapping.Identity)

    @Immutable
    data object Offset : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int = offset

        override fun transformedToOriginal(offset: Int): Int = (offset - 1).coerceAtLeast(0)

    }
}