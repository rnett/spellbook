package com.rnett.spellbook.data

import androidx.compose.runtime.Composable
import com.rnett.spellbook.model.spellbook.Spellbook
import kotlinx.serialization.Serializable

@Serializable
data class LoadedSpellbook(val reference: SpellbookReference, val spellbook: Spellbook)

@Serializable
data class SpellbookDaoKey(val key: String)

@Serializable
data class SpellbookReference(val daoKey: SpellbookDaoKey, val name: String) {

    val dao by lazy { SpellbookDaoLoader[daoKey] }

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

object SpellbookDaoLoader {
    val daos: List<SpellbooksDao> = spellbookDaos()
    val daosByKey = daos.associateBy { it.key }

    operator fun get(key: SpellbookDaoKey): SpellbooksDao =
        daosByKey[key] ?: throw IllegalStateException("No spellbook dao for key $key")

    init {
        val tooMany = daos.groupBy { it.key }.filter { it.value.size > 1 }
        if (tooMany.isNotEmpty()) {
            throw IllegalStateException("Some spellbook daos have duplicate keys: $tooMany")
        }
    }
}

internal expect fun spellbookDaos(): List<SpellbooksDao>

interface SpellbooksDao {
    val name: String
    val key: SpellbookDaoKey

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