package com.rnett.spellbook

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import com.rnett.spellbook.filter.SpellFilter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

inline fun Iterable<String>.newName(base: String = "New Search", addon: (Int) -> String = { " $it" }): String {
    var name = base
    var i = 0
    while (name in this) {
        i++
        name = base + addon(i)
    }
    return name
}

class LocalSavedSearchRepo(val handleException: (Throwable) -> Unit) : SavedSearchRepository, CoroutineScope by CoroutineScope(
    Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { _, e ->
        handleException(e)
    }) {
    init {
        File("./data").mkdirs()
    }

    private val file: File = File("./data/savedSearches.json")

    private val json = Json { }

    private val local: MutableList<Pair<String, SpellFilter>> by lazy {
        if (file.exists()) {
            json.decodeFromString<Map<String, SpellFilter>>(file.readText()).toList().toMutableStateList()
        } else {
            mutableStateListOf()
        }
    }

    override val all: List<Pair<String, SpellFilter>> = local

    private var saverJob: Job? = null
    private fun save() {
        val data = json.encodeToString<Map<String, SpellFilter>>(local.toMap())
        saverJob?.cancel("Obsolete")

        saverJob = launch() {
            file.writeText(data)
        }
    }

    override fun remove(idx: Int) {
        local.removeAt(idx)
        save()
    }

    override fun rename(idx: Int, new: String) {
        local[idx] = new to local[idx].second
        save()
    }

    override fun add(name: String, filter: SpellFilter): Int {
        local.add(name to filter)
        save()
        return local.lastIndex
    }

    private fun setHelper(name: String, search: SpellFilter) {
        val index = local.indexOfFirst { it.first == name }
        if (index >= 0) {
            local[index] = name to search
        } else {
            local.add(name to search)
        }
    }

    override fun set(idx: Int, value: SpellFilter) {
        local[idx] = local[idx].first to value
        save()
    }

    override fun get(idx: Int): Pair<String, SpellFilter> = local[idx]
}