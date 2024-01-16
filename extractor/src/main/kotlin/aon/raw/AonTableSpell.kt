package com.rnett.spellbook.extractor.aon.raw

import com.rnett.spellbook.extractor.Resources
import com.rnett.spellbook.extractor.Serialization
import com.rnett.spellbook.extractor.aon.aonClient
import com.rnett.spellbook.extractor.aon.processed.CastingActions
import com.rnett.spellbook.extractor.aon.processed.EnumLikeProcessor
import com.rnett.spellbook.extractor.copy
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.json.*

@Suppress("PropertyName")
@Serializable
data class AonIndexedSpell(
    val _index: String,
    val _id: String,
    @Serializable(AonTableSpell.TransformingSerializer::class) val _source: AonTableSpell,
    val sort: JsonArray,
    val _score: Int?
)

@Serializable
data class AonResponse<T>(val hits: AonResult<T>)

@Serializable
data class AonResult<T>(val hits: T)

@Suppress("PropertyName")
@Serializable
data class AonTableSpell(
    @get:EnumLike
    val actions: String,
    @get:EnumLike
    val area: String? = null,
    @get:EnumLike
    val bloodline: List<String> = emptyList(),
    @get:EnumLike
    val deity: List<String> = emptyList(),
    @get:EnumLike
    val domain: List<String> = emptyList(),
    @get:EnumLike
    val mystery: List<String> = emptyList(),
    @get:EnumLike
    val lesson: List<String> = emptyList(),
    @get:EnumLike
    val patron_theme: List<String> = emptyList(),
    val duration: Int? = null,
    @get:EnumLike
    val duration_raw: String? = null,
    @get:EnumLike
    val category: String,
    @get:EnumLike
    val component: List<String>? = null,
    val heighten: List<String>? = null,
    val heighten_level: List<Int>? = null,
    val id: String,
    val level: Int,
    val markdown: String,
    val name: String,
    val pfs: String? = null,
    val range: Int? = null,
    @get:EnumLike
    val range_raw: String? = null,
    val rarity: String,
    val rarity_id: Int,
    val release_date: String? = null,
    @get:EnumLike
    val school: String? = null,
    val source: List<String>,
    val source_raw: List<String>,
    val source_category: String,
    val source_group: String? = null,
    val summary: String,
    val summary_markdown: String,
    @get:EnumLike
    val target: String? = null,
    @get:EnumLike
    val tradition: List<String>? = null,
    val trait: List<String>,
    @get:EnumLike
    val type: String,
    val url: String,
    @get:EnumLike
    val saving_throw: String? = null,
    @get:EnumLike
    val element: List<String>? = null,
    val trigger: String? = null,
    val spoilers: String? = null,
    val requirement: String? = null,
    val cost: String? = null
) {

    val spellId by lazy {
        id.removePrefix("spell-").toInt()
    }

    @OptIn(ExperimentalSerializationApi::class)
    object TransformingSerializer : JsonTransformingSerializer<AonTableSpell>(serializer()) {
        private val ignoredProperties = setOf("exclude_from_search", "breadcrumbs_spa")
        private val ignoredIfNothing = setOf("resistance", "weakness", "speed")

        private fun JsonElement.isNothing(): Boolean =
            when (this) {
                is JsonArray -> isEmpty()
                is JsonObject -> isEmpty()
                is JsonPrimitive -> this is JsonNull
            }

        private val properties = descriptor.elementNames.toSet()

        override fun transformDeserialize(element: JsonElement): JsonElement {
            val obj = element.jsonObject

            val missingProperties = obj.keys - properties
            val notReallyMissing = missingProperties.filter {
                it in ignoredProperties ||
                        it.endsWith("_markdown") ||
                        it.endsWith("_raw") ||
                        (it in ignoredIfNothing && obj[it]!!.isNothing())
            }

            return JsonObject(obj.filterNot { it.key in notReallyMissing })
        }
    }

    companion object {
        private val spellQuery =
            Serialization.Lenient.parseToJsonElement(Resources.readText("spell_query.json")).jsonObject

        private fun buildQuery(batch: Int, after: JsonArray? = null): JsonObject = spellQuery.copy {
            this.put("size", batch)
            if (after != null)
                this.put("search_after", after)
        }

        suspend fun loadSpells(
            client: HttpClient,
            batch: Int,
            after: JsonArray? = null
        ): List<AonIndexedSpell> {
            val response = client.post("https://elasticsearch.aonprd.com/aon/_search") {
                contentType(ContentType.Application.Json)
                setBody(buildQuery(batch, after))
            }.body<AonResponse<List<JsonObject>>>()

            return response.hits.hits.map {
                val name = it["_source"]!!.jsonObject["name"]!!.jsonPrimitive.content
                try {
                    Serialization.Lenient.decodeFromJsonElement(it)
                } catch (t: Throwable) {
                    throw IllegalStateException("Error parsing spell $name", t)
                }
            }
        }

        suspend fun loadAllSpells(client: HttpClient, batch: Int): Flow<AonTableSpell> {
            return flow {
                var nextStart: JsonArray? = null
                while (currentCoroutineContext().isActive) {
                    val spells = loadSpells(client, batch, nextStart)
                    spells.forEach { emit(it._source) }
                    if (spells.size < batch)
                        break

                    nextStart = spells.last().sort
                }
            }.buffer(batch)
        }

        @JvmStatic
        fun main(args: Array<String>) = runBlocking {

            val spells = loadAllSpells(aonClient, 50).toList()

            val actions = EnumLikeProcessor.batchProcess<CastingActions>(spells.associate { it.spellId to it.actions })

            println(actions)
        }
    }
}