package com.rnett.spellbook.data

import com.rnett.spellbook.model.spellbook.Spellbook
import java.nio.file.Path
import javax.swing.filechooser.FileSystemView
import kotlin.io.path.*

actual object SpellbookDaoLoader {
    actual val daos: List<SpellbooksDao>
        get() = listOf(
            FileSpellbooksDao(
                "Local",
                FileSystemView.getFileSystemView().defaultDirectory.toPath().resolve("PF2E-Spellbook")
            )
        )
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
            LoadedSpellbook(this, it.second.fileName.toString().removeSuffix(suffix), it.first)
        }
    }

    private fun file(key: String): Path = baseDir.resolve(key + suffix)

    override suspend fun loadSpellbook(key: String): LoadedSpellbook? {
        val file = file(key)
        if (!file.exists())
            return null
        return SpellbookSerialization.tryRead(file.readText())?.let { LoadedSpellbook(this, key, it) }
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

            if (oldName != null) {
                val oldFile = baseDir.resolve(oldName + suffix)
                oldFile.deleteIfExists()
            }

            return LoadedSpellbook(this, newName, spellbook)
        }
        return null
    }

    override suspend fun delete(name: String) {
        file(name).deleteIfExists()
    }
}