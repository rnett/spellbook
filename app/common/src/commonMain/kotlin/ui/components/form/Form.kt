package com.rnett.spellbook.ui.components.form

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import kotlin.jvm.JvmName

@Stable
abstract class FormModel<T : Any> {
    private val fields: MutableList<FormField<*, *>> = mutableStateListOf()
    private val subForms: MutableList<Subform<*, *, *>> = mutableStateListOf()

    protected fun registerField(field: FormField<*, *>) {
        fields += field
    }

    private fun registerSubform(formModel: Subform<*, *, *>) {
        subForms += formModel
    }

    protected fun <T, R> field(initialValue: T, validator: Validation<T, R>): Lazy<FormField<T, R>> =
        lazyOf(FormFieldImpl(initialValue, validator).also(this::registerField))

    @JvmName("sameTypeField")
    protected fun <T> field(initialValue: T, validator: Validation<T, T> = Validation.None()): Lazy<FormField<T, T>> =
        lazyOf(FormFieldImpl(initialValue, validator).also(this::registerField))

    protected fun <F : FormModel<R>, R : Any, S> subform(
        form: F,
        validation: Validation<R?, S>
    ): Lazy<Subform<F, R, S>> =
        lazyOf(SubformImpl(form, validation).also(this::registerSubform))

    protected fun <F : FormModel<R>, R : Any> optionalSubform(form: F): Lazy<Subform<F, R, R?>> =
        subform(form, Validation.None())

    protected fun <F : FormModel<R>, R : Any> requiredSubform(form: F): Lazy<Subform<F, R, R>> =
        subform(form, Validation.NotNull())

    protected fun <F : FormModel<R>, R : Any> maybeRequiredSubform(
        form: F,
        required: () -> Boolean
    ): Lazy<Subform<F, R, R?>> =
        subform(form) {
            if (it == null && required())
                Validated.Invalid("Sub-form is invalid")
            else
                Validated.Valid(it)
        }

    val isValid: Boolean by derivedStateOf { fields.all { it.isValid } && subForms.all { it.isValid } }

    private class ValidatedValues(
        val fieldValues: Map<FormField<*, *>, Any?>,
        val formValues: Map<Subform<*, *, *>, Any?>
    ) : Values {
        override fun <R> get(field: FormField<*, R>): R = fieldValues[field] as R
        override fun <R> get(subform: Subform<*, *, R>): R = formValues[subform] as R
    }

    interface Values {
        operator fun <R> get(field: FormField<*, R>): R
        operator fun <R> get(subform: Subform<*, *, R>): R

        val <R> FormField<*, R>.value get() = this@Values[this]
        val <R> Subform<*, *, R>.value get() = this@Values[this]
    }

    protected abstract fun Values.build(): T

    fun buildIfValid(): T? {
        val (values, forms) = Snapshot.withMutableSnapshot {
            fields.associateWith { it.validated } to subForms.associateWith { it.validated }
        }
        if (values.values.any { it.isInvalid() })
            return null

        if (forms.values.any { it.isInvalid() })
            return null

        val validValues =
            ValidatedValues(values.mapValues { it.value.valueOrNull }, forms.mapValues { it.value.valueOrNull })
        return validValues.build()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FormModel<*>) return false

        if (fields != other.fields) return false

        return true
    }

    override fun hashCode(): Int {
        return fields.hashCode()
    }
}

interface Subform<F : FormModel<T>, T : Any, R> : FormValue<R> {
    val form: F
}

private class SubformImpl<F : FormModel<T>, T : Any, R>(
    override val form: F,
    private val validation: Validation<T?, R>
) : Subform<F, T, R> {
    override val validated: Validated<R> by derivedStateOf {
        validation.validate(form.buildIfValid())
    }

}

@Stable
private class FormFieldImpl<T, R>(
    initialUnderlyingValue: T,
    val validation: Validation<T, R>
) : FormField<T, R> {
    override val underlyingState = mutableStateOf(initialUnderlyingValue)
    private val validatedState = derivedStateOf {
        validation.validate(underlyingState.value)
    }

    override fun update(value: T) {
        underlyingState.value = value
    }

    override var underlying: T by underlyingState
    override val validated: Validated<R> by validatedState
}