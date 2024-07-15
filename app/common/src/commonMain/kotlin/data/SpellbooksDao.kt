package com.rnett.spellbook.data

import androidx.compose.runtime.Composable
import com.rnett.spellbook.model.spellbook.Spellbook

data class LoadedSpellbook(val reference: SpellbookReference, val spellbook: Spellbook)

data class SpellbookReference(val dao: SpellbooksDao, val name: String) {
    suspend fun load(): Spellbook? = dao.loadSpellbook(name)?.spellbook
    suspend fun save(spellbook: Spellbook): LoadedSpellbook? {
        require(spellbook.name == name) { "Can't save a different spellbook" }
        return dao.saveSpellbook(name, spellbook)
    }
}

data class SpellbookMetadata(val reference: SpellbookReference)

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