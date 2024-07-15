package com.rnett.spellbook.ui.pages.spellbooks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rnett.spellbook.data.SpellbookReference
import com.rnett.spellbook.model.spellbook.Spellbook
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Stable
class SpellbookEditScreenModel(
    val spellbookReference: SpellbookReference,
    initialSpellbook: Spellbook
) : ScreenModel {
    private val spellbookFlow = MutableStateFlow(initialSpellbook)

    val spellbook @Composable get() = spellbookFlow.collectAsState()

    init {
        screenModelScope.launch {
            spellbookFlow.collectLatest {
                withContext(NonCancellable) {
                    spellbookReference.dao.saveSpellbook(it.name, it)
                }
            }
        }
    }

    fun update(spellbook: Spellbook) {
        spellbookFlow.value = spellbook
    }

    fun update(spellbook: (Spellbook) -> Spellbook) {
        spellbookFlow.updateAndGet(spellbook)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SpellbookEditScreenModel) return false

        if (spellbookReference != other.spellbookReference) return false
        if (spellbookFlow != other.spellbookFlow) return false

        return true
    }

    override fun hashCode(): Int {
        var result = spellbookReference.hashCode()
        result = 31 * result + spellbookFlow.hashCode()
        return result
    }

    companion object {
        @Composable
        fun model() =
            LocalNavigator.currentOrThrow.rememberNavigatorScreenModel<SpellbookEditScreenModel> { error("Must already be initialized") }
    }
}