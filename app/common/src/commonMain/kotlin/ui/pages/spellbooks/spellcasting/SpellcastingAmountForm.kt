package com.rnett.spellbook.ui.pages.spellbooks.spellcasting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.model.spellbook.SpellcastingAmount
import com.rnett.spellbook.ui.components.IntField
import com.rnett.spellbook.ui.components.LabeledCheckbox
import com.rnett.spellbook.ui.components.PlaceholderTransformation
import com.rnett.spellbook.ui.components.RequiredText
import com.rnett.spellbook.ui.components.form.FormModel
import com.rnett.spellbook.ui.components.form.Validation
import com.rnett.spellbook.ui.components.form.ifPresent
import com.rnett.spellbook.ui.components.form.plus

class SpellcastingAmountForm() : FormModel<SpellcastingAmount>() {
    val archetype by field(false)
    val bounded by field(false)

    val boundedArchetypeForm by maybeRequiredSubform(BoundedArchetypeForm()) { bounded.validated.valueOrNull == true && archetype.validated.valueOrNull == true }
    val boundedForm by maybeRequiredSubform(BoundedForm()) { bounded.validated.valueOrNull == true && archetype.validated.valueOrNull == false }

    val fullArchetypeForm by maybeRequiredSubform(FullArchetypeForm()) { bounded.validated.valueOrNull == false && archetype.validated.valueOrNull == true }
    val fullForm by maybeRequiredSubform(FullForm()) { bounded.validated.valueOrNull == false && archetype.validated.valueOrNull == false }

    override fun Values.build(): SpellcastingAmount {
        return if (bounded.value) {
            if (archetype.value)
                boundedArchetypeForm.value!!
            else
                boundedForm.value!!
        } else {
            if (archetype.value)
                fullArchetypeForm.value!!
            else
                fullForm.value!!
        }
    }

    open class BoundedArchetypeForm : FormModel<SpellcastingAmount>() {
        val dedicationAt by field("", Validation.NotBlank + Validation.ToInt + Validation.Max({ 20 }))
        val basicAt by field(
            "",
            Validation.ToIntIfPresent +
                    Validation.Max({ 20 }).ifPresent() +
                    Validation.Min({ dedicationAt.validated.valueOrNull }).ifPresent()
        )
        val expertAt by field(
            "",
            Validation.ToIntIfPresent +
                    Validation.Max({ 20 }).ifPresent() +
                    Validation.Min({ basicAt.validated.valueOrNull }).ifPresent()
        )
        val masterAt by field(
            "",
            Validation.ToIntIfPresent +
                    Validation.Max({ 20 }).ifPresent() +
                    Validation.Min({ expertAt.validated.valueOrNull }).ifPresent()
        )

        override fun Values.build(): SpellcastingAmount {
            return SpellcastingAmount.BoundedArchetype(
                dedicationAt.value,
                basicAt.value,
                expertAt.value,
                masterAt.value
            )
        }
    }

    class FullArchetypeForm : BoundedArchetypeForm() {
        val breadthAt by field(
            "",
            Validation.ToIntIfPresent +
                    Validation.Max({ 20 }).ifPresent() +
                    Validation.Min({ dedicationAt.validated.valueOrNull }).ifPresent()
        )

        override fun Values.build(): SpellcastingAmount {
            return SpellcastingAmount.FullArchetype(
                dedicationAt.value,
                basicAt.value,
                expertAt.value,
                masterAt.value,
                breadthAt.value
            )
        }
    }

    class FullForm() : BoundedForm() {
        val tenthSlotFeature by field(false)
        val tenthSlotFeat by field(false)

        override fun Values.build(): SpellcastingAmount =
            SpellcastingAmount.Full(slotsPerRank.value, tenthSlotFeature.value, tenthSlotFeat.value)
    }

    open class BoundedForm() : FormModel<SpellcastingAmount>() {
        val slotsPerRank by field("", Validation.NotBlank + Validation.ToInt + Validation.Min({ 0 }))

        override fun Values.build(): SpellcastingAmount =
            SpellcastingAmount.Bounded(slotsPerRank.value)
    }
}

@Composable
fun SpellcastingAmountForm(form: SpellcastingAmountForm) {
    if (form.archetype.underlying) {
        ArchetypeAmountForm(if (form.bounded.underlying) form.boundedArchetypeForm.form else form.fullArchetypeForm.form)
    } else {
        FullAmountForm(if (form.bounded.underlying) form.boundedForm.form else form.fullForm.form)
    }
}

@Composable
private fun FullAmountForm(form: SpellcastingAmountForm.BoundedForm) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IntField(
            form.slotsPerRank,
            label = { Text("Slots per rank") },
            visualTransformation = PlaceholderTransformation,
        )

        if (form is SpellcastingAmountForm.FullForm) {
            Spacer(Modifier.width(20.dp))

            LabeledCheckbox(form.tenthSlotFeature.underlying, { form.tenthSlotFeature.underlying = it }) {
                Text("Rank 10 slot class feature")
            }

            Spacer(Modifier.width(20.dp))

            LabeledCheckbox(form.tenthSlotFeat.underlying, { form.tenthSlotFeat.underlying = it }) {
                Text("Rank 10 slot feat")
            }
        }
    }
}

@Composable
private fun ArchetypeAmountForm(form: SpellcastingAmountForm.BoundedArchetypeForm) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Column {
            IntField(
                form.dedicationAt,
                label = { RequiredText("Dedication feat taken at") },
                visualTransformation = PlaceholderTransformation,
            )
            IntField(
                form.basicAt,
                label = { Text("Basic feat taken at") },
                visualTransformation = PlaceholderTransformation,
            )
            IntField(
                form.expertAt,
                label = { Text("Expert feat taken at") },
                visualTransformation = PlaceholderTransformation,
            )
            IntField(
                form.masterAt,
                label = { Text("Master feat taken at") },
                visualTransformation = PlaceholderTransformation,
            )
        }

        if (form is SpellcastingAmountForm.FullArchetypeForm) {
            IntField(
                form.breadthAt,
                label = { Text("Breadth feat taken at") },
                visualTransformation = PlaceholderTransformation,
            )
        }
    }
}