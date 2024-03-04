package com.rnett.spellbook.data

import androidx.compose.runtime.Composable
import com.rnett.spellbook.model.spellbook.Spellbook

data class LoadedSpellbook(val dao: SpellbooksDao, val name: String, val spellbook: Spellbook)

data class SpellbookDaoDisplay(
    val leadingIcon: (@Composable () -> Unit)? = null,
    val trainingIcon: (@Composable () -> Unit)? = null,
)

expect object SpellbookDaoLoader {
    val daos: List<SpellbooksDao>
}

interface SpellbooksDao {
    val name: String

    val display: SpellbookDaoDisplay get() = SpellbookDaoDisplay()

    suspend fun listSpellbooks(): List<LoadedSpellbook>
    suspend fun loadSpellbook(name: String): LoadedSpellbook?

    suspend fun isNewNameValid(name: String): Boolean

    /**
     * Returns null if a new name is used, but spellbook with the new key already exists
     */
    suspend fun saveSpellbook(oldName: String?, spellbook: Spellbook): LoadedSpellbook?
    suspend fun delete(name: String)

}