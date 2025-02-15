package com.rnett.spellbook.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import com.rnett.spellbook.ui.components.form.FormField

val INT_REGEX = Regex("[\\d-]*")


@Composable
inline fun IntField(
    formField: FormField<String, *>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    noinline label: @Composable() (() -> Unit)? = null,
    noinline placeholder: @Composable() (() -> Unit)? = null,
    noinline leadingIcon: @Composable() (() -> Unit)? = null,
    noinline trailingIcon: @Composable() (() -> Unit)? = null,
    noinline prefix: @Composable() (() -> Unit)? = null,
    noinline suffix: @Composable() (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors()
) {
    IntField(
        formField.underlying,
        { formField.underlying = it },
        modifier,
        enabled,
        readOnly,
        textStyle,
        label,
        placeholder,
        leadingIcon,
        trailingIcon,
        prefix,
        suffix,
        formField.validated.errorOrNull?.let { { Text(it) } },
        formField.hasError,
        visualTransformation,
        keyboardOptions,
        keyboardActions,
        interactionSource,
        shape,
        colors
    )
}

@Composable
inline fun IntField(
    value: String,
    crossinline update: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    noinline label: @Composable (() -> Unit)? = null,
    noinline placeholder: @Composable (() -> Unit)? = null,
    noinline leadingIcon: @Composable (() -> Unit)? = null,
    noinline trailingIcon: @Composable (() -> Unit)? = null,
    noinline prefix: @Composable (() -> Unit)? = null,
    noinline suffix: @Composable (() -> Unit)? = null,
    noinline supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors()
) {
    TextField(
        value = value,
        onValueChange = {
            if (it.matches(INT_REGEX))
                update(it)
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
        keyboardOptions = keyboardOptions.copy(keyboardType = KeyboardType.Number),
        singleLine = true,
    )
}

@Composable
inline fun IntField(
    value: Int?,
    crossinline update: (Int?) -> Unit,
    minimum: Int? = null,
    maximum: Int? = null,
    onlyValid: Boolean = true,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    noinline label: @Composable (() -> Unit)? = null,
    noinline placeholder: @Composable (() -> Unit)? = null,
    noinline leadingIcon: @Composable (() -> Unit)? = null,
    noinline trailingIcon: @Composable (() -> Unit)? = null,
    noinline prefix: @Composable (() -> Unit)? = null,
    noinline suffix: @Composable (() -> Unit)? = null,
    noinline supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors()
) {
    if (minimum != null && maximum != null && minimum > maximum)
        throw IllegalArgumentException("Minimum ($minimum) must be greater than maximum ($maximum)")

    var realValue by remember { mutableStateOf(value?.toString().orEmpty()) }
    value?.let { realValue = it.toString() }

    val intValue = realValue.toIntOrNull()

    val tooSmall = intValue != null && minimum != null && intValue < minimum
    val tooLarge = intValue != null && maximum != null && intValue > maximum

    TextField(
        value = realValue,
        onValueChange = {
            Snapshot.withMutableSnapshot {
                if (!it.matches(INT_REGEX))
                    return@withMutableSnapshot

                realValue = it
                if (it.isEmpty()) {
                    update(null)
                } else {
                    it.toIntOrNull()?.let { new ->
                        if (!onlyValid)
                            update(new)
                        else {
                            if ((maximum == null || new < maximum) && (minimum == null || new > minimum))
                                update(new)
                            else
                                update(null)
                        }
                    } ?: update(null)
                }
            }
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = {
            if (tooSmall)
                Text("Must be > $minimum")
            else if (tooLarge)
                Text("Must be < $maximum")
            else
                supportingText?.invoke()
        },
        isError = isError || tooSmall || tooLarge,
        visualTransformation = visualTransformation,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
        keyboardOptions = keyboardOptions.copy(keyboardType = KeyboardType.Number),
        singleLine = true,
    )
}

@Composable
inline fun SmallIntField(
    value: Int?,
    crossinline update: (Int?) -> Unit,
    minimum: Int? = null,
    maximum: Int? = null,
    onlyValid: Boolean = true,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    noinline label: @Composable (() -> Unit)? = null,
    noinline placeholder: @Composable (() -> Unit)? = null,
    noinline leadingIcon: @Composable (() -> Unit)? = null,
    noinline trailingIcon: @Composable (() -> Unit)? = null,
    noinline prefix: @Composable (() -> Unit)? = null,
    noinline suffix: @Composable (() -> Unit)? = null,
    noinline supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors()
) {
    if (minimum != null && maximum != null && minimum > maximum)
        throw IllegalArgumentException("Minimum ($minimum) must be greater than maximum ($maximum)")

    var realValue by remember { mutableStateOf(value?.toString().orEmpty()) }
    value?.let { realValue = it.toString() }

    val intValue = realValue.toIntOrNull()

    val tooSmall = intValue != null && minimum != null && intValue < minimum
    val tooLarge = intValue != null && maximum != null && intValue > maximum

    SmallTextField(
        value = realValue,
        onValueChange = {
            Snapshot.withMutableSnapshot {
                if (!it.matches(INT_REGEX))
                    return@withMutableSnapshot

                realValue = it
                if (it.isEmpty()) {
                    update(null)
                } else {
                    it.toIntOrNull()?.let { new ->
                        if (!onlyValid)
                            update(new)
                        else {
                            if ((maximum == null || new < maximum) && (minimum == null || new > minimum))
                                update(new)
                            else
                                update(null)
                        }
                    } ?: update(null)
                }
            }
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = {
            if (tooSmall)
                Text("Must be > $minimum")
            else if (tooLarge)
                Text("Must be < $maximum")
            else
                supportingText?.invoke()
        },
        isError = isError || tooSmall || tooLarge,
        visualTransformation = visualTransformation,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
        keyboardOptions = keyboardOptions.copy(keyboardType = KeyboardType.Number),
        singleLine = true,
    )
}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//inline fun SmallIntField(
//    value: Int?,
//    crossinline update: (Int?) -> Unit,
//    minimum: Int? = null,
//    maximum: Int? = null,
//    onlyValid: Boolean = true,
//    modifier: Modifier = Modifier,
//    enabled: Boolean = true,
//    readOnly: Boolean = false,
//    isError: Boolean = false,
//    noinline errorText: @Composable (() -> Unit)? = null,
//    textStyle: TextStyle = LocalTextStyle.current,
//    visualTransformation: VisualTransformation = VisualTransformation.None,
//    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
//    keyboardActions: KeyboardActions = KeyboardActions.Default,
//    interactionSource: MutableInteractionSource? = null,
//    colors: TextFieldColors = TextFieldDefaults.colors()
//) {
//    if (minimum != null && maximum != null && minimum > maximum)
//        throw IllegalArgumentException("Minimum ($minimum) must be greater than maximum ($maximum)")
//
//    var realValue by remember { mutableStateOf(value?.toString().orEmpty()) }
//    value?.let { realValue = it.toString() }
//
//    val intValue = realValue.toIntOrNull()
//
//    val tooSmall = intValue != null && minimum != null && intValue < minimum
//    val tooLarge = intValue != null && maximum != null && intValue > maximum
//
//    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
//    val focused = interactionSource.collectIsFocusedAsState().value
//
//    val isReallyError = isError || tooSmall || tooLarge
//
//    val textColor =
//        textStyle.color.takeOrElse {
//            colors.textColor(enabled, isReallyError, focused)
//        }
//
//    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
//
//    val borderColor = colors.indicatorColor(enabled, isReallyError, focused)
//
//    CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
//        Surface(
//            color = colors.containerColor(enabled, isReallyError, focused),
//            border = if (isReallyError) BorderStroke(0.3.dp, borderColor) else null
//        ) {
//            val tooltipState = rememberTooltipState(false)
//
//            LaunchedEffect(tooltipState, isReallyError) {
//                if (isReallyError)
//                    tooltipState.show()
//                else
//                    tooltipState.dismiss()
//            }
//
//            val tooltipPosition = TooltipDefaults.rememberPlainTooltipPositionProvider()
//            TooltipBox(tooltipPosition, {
//                if (isReallyError) {
//                    RichTooltip(title = { Text("Error") }) {
//                        if (tooSmall)
//                            Text("Must be > $minimum")
//                        else if (tooLarge)
//                            Text("Must be < $maximum")
//                        else
//                            errorText?.invoke()
//                    }
//                }
//            }, tooltipState, focusable = false) {
//                BasicTextField(
//                    value = realValue,
//                    onValueChange = {
//                        Snapshot.withMutableSnapshot {
//                            if (!it.matches(INT_REGEX))
//                                return@withMutableSnapshot
//
//                            realValue = it
//                            if (it.isEmpty()) {
//                                update(null)
//                            } else {
//                                it.toIntOrNull()?.let { new ->
//                                    if (!onlyValid)
//                                        update(new)
//                                    else {
//                                        if ((maximum == null || new < maximum) && (minimum == null || new > minimum))
//                                            update(new)
//                                        else
//                                            update(null)
//                                    }
//                                } ?: update(null)
//                            }
//                        }
//                    },
//                    modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp).width(26.dp).then(modifier),
//                    enabled = enabled,
//                    readOnly = readOnly,
//                    textStyle = mergedTextStyle,
//                    visualTransformation = visualTransformation,
//                    keyboardActions = keyboardActions,
//                    interactionSource = interactionSource,
//                    cursorBrush = SolidColor(colors.cursorColor(isReallyError)),
//                    keyboardOptions = keyboardOptions.copy(keyboardType = KeyboardType.Number),
//                    singleLine = true,
//                )
//            }
//        }
//    }
//
//}

@Stable
fun TextFieldColors.textColor(
    enabled: Boolean,
    isError: Boolean,
    focused: Boolean,
): Color =
    when {
        !enabled -> disabledTextColor
        isError -> errorTextColor
        focused -> focusedTextColor
        else -> unfocusedTextColor
    }

@Stable
fun TextFieldColors.containerColor(
    enabled: Boolean,
    isError: Boolean,
    focused: Boolean,
): Color =
    when {
        !enabled -> disabledContainerColor
        isError -> errorContainerColor
        focused -> focusedContainerColor
        else -> unfocusedContainerColor
    }

@Stable
fun TextFieldColors.indicatorColor(
    enabled: Boolean,
    isError: Boolean,
    focused: Boolean,
): Color =
    when {
        !enabled -> disabledIndicatorColor
        isError -> errorIndicatorColor
        focused -> focusedIndicatorColor
        else -> unfocusedIndicatorColor
    }

@Stable
fun TextFieldColors.cursorColor(isError: Boolean): Color =
    if (isError) errorCursorColor else cursorColor