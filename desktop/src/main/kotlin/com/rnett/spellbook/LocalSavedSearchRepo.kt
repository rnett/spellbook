package com.rnett.spellbook

import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.spellbook.Spellbook
import kotlinx.collections.immutable.PersistentMap
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.io.File

class LocalNamedObjectRepo<T>(
    private val file: File,
    private val handleException: (Throwable) -> Unit,
    serializer: KSerializer<T>,
    baseName: String,
    private val json: Json = Json { },
) : NamedObjectRepository<T>(
    json.decodeFromString(MapSerializer(String.serializer(), serializer), file.readText()),
    baseName
), CoroutineScope by CoroutineScope(
    Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { _, e ->
        handleException(e)
    }) {
    init {
        file.parentFile.mkdirs()
    }

    private val mapSerializer: KSerializer<Map<String, T>> = MapSerializer(String.serializer(), serializer)

    private var saverJob: Job? = null
    private fun save(value: Map<String, T>) {
        @Suppress("RemoveExplicitTypeArguments")
        val data = json.encodeToString(mapSerializer, value)
        saverJob?.cancel("Obsolete")

        saverJob = launch() {
            file.writeText(data)
        }
    }

    override fun onUpdate(value: PersistentMap<String, T>) {
        save(value)
    }
}

fun LocalSavedSearchRepo(handleException: (Throwable) -> Unit) =
    LocalNamedObjectRepo<SpellFilter>(File("./data/savedSearches.json"), handleException, SpellFilter.serializer(), "New Search")

fun LocalSpellbookhRepo(handleException: (Throwable) -> Unit) =
    LocalNamedObjectRepo<Spellbook>(File("./data/spellbooks.json"), handleException, Spellbook.serializer(), "New Spellbook")