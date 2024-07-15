package com.rnett.spellbook.data

import com.rnett.spellbook.model.spellbook.Spellbook
import java.nio.file.Path
import javax.swing.filechooser.FileSystemView
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.readText
import kotlin.io.path.writeText

actual object SpellbookDaoLoader {
    actual val daos: List<SpellbooksDao> by lazy {
        listOf(
            FileSpellbooksDao(
                "Local",
                FileSystemView.getFileSystemView().defaultDirectory.toPath().resolve("PF2E-Spellbook")
            )
        )
    }
}

class FileSpellbooksDao(override val name: String, val baseDir: Path) : SpellbooksDao {
    companion object {
        const val suffix: String = ".spellbook.json"
    }

    init {
        baseDir.createDirectories()
    }

    override suspend fun listSpellbooks(): List<LoadedSpellbook> {
        return baseDir.listDirectoryEntries("*$suffix").mapNotNull { file ->
            SpellbookSerialization.tryRead(file.readText())?.let { it to file }
        }.map {
            LoadedSpellbook(SpellbookReference(this, it.first.name), it.first)
        }
    }

    private fun file(key: String): Path = baseDir.resolve(key + suffix)

    override suspend fun loadSpellbook(key: String): LoadedSpellbook? {
        val file = file(key)
        if (!file.exists())
            return null
        return SpellbookSerialization.tryRead(file.readText())
            ?.let { LoadedSpellbook(SpellbookReference(this, key), it) }
    }

    override suspend fun isNewNameValid(name: String): Boolean {
        if (name.isBlank())
            return false

        if (name != name.trim())
            return false

        return !file(name).exists()
    }

    override suspend fun saveSpellbook(oldName: String?, spellbook: Spellbook): LoadedSpellbook? {
        val newName = spellbook.name
        val file = file(newName)
        if (newName == oldName || !file.exists()) {
            file.writeText(SpellbookSerialization.write(spellbook))

            if (oldName != null && oldName != newName) {
                val oldFile = file(oldName)
                oldFile.deleteIfExists()
            }

            return LoadedSpellbook(SpellbookReference(this, newName), spellbook)
        }
        return null
    }

    override suspend fun delete(name: String) {
        file(name).deleteIfExists()
    }
}