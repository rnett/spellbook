package com.rnett.spellbook

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = NamedListSerializer::class)
interface NamedList<T> : List<Pair<String, T>> {
    operator fun get(name: String): T?
    operator fun contains(name: String): Boolean
    fun newName(baseName: String, suffix: (Int) -> String): String

    fun indexOf(name: String): Int

    companion object {
        internal var listFactory: (Int) -> MutableList<Pair<String, *>> = { ArrayList(it) }
        internal var mapFactory: (Int) -> MutableMap<String, Int> = { LinkedHashMap(it) }

        fun setListFactory(factory: (Int) -> MutableList<Pair<String, *>>) {
            listFactory = factory
        }

        fun setMapFactory(factory: (Int) -> MutableMap<String, Int>) {
            mapFactory = factory
        }

    }
}

interface MutableNamedList<T> : NamedList<T> {
    operator fun set(name: String, value: T): T?
    fun setAll(items: Map<String, T>)
    fun setIndex(name: String, newIndex: Int): Boolean

    fun remove(name: String): T?

    fun rename(oldName: String, newName: String): Boolean

    fun swap(name1: String, name2: String): Boolean
}

class NamedListImpl<T>(
    @Suppress("UNCHECKED_CAST") private val backingList: MutableList<Pair<String, T>> = NamedList.listFactory(0) as MutableList<Pair<String, T>>,
    private val indices: MutableMap<String, Int> = NamedList.mapFactory(backingList.size)
) : MutableNamedList<T>,
    List<Pair<String, T>> by backingList {


    init {
        indices.apply {
            backingList.forEachIndexed { idx, (name, _) ->
                this[name] = idx
            }
        }
    }

    override fun get(name: String): T? {
        return indices[name]?.let { backingList[it].second }
    }

    override fun newName(baseName: String, suffix: (Int) -> String): String {
        var name = baseName
        var i = 0
        while (name in this) {
            i++
            name = baseName + suffix(i)
        }
        return name
    }

    override fun set(name: String, value: T): T? {
        return if (name in indices) {
            val index = indices[name]!!
            val entry = backingList[index]
            backingList[index] = name to value
            entry.second
        } else {
            indices[name] = backingList.size
            backingList.add(name to value)
            null
        }
    }

    override fun setAll(items: Map<String, T>) {
        items.forEach {
            this[it.key] = it.value
        }
    }

    override fun rename(oldName: String, newName: String): Boolean {
        val index = indices[oldName] ?: return false
        backingList[index] = newName to backingList[index].second
        indices.remove(oldName)
        indices[newName] = index
        return true
    }

    override fun contains(name: String): Boolean {
        return name in indices
    }

    override fun remove(name: String): T? {
        val idx = indices.remove(name) ?: return null
        val (_, item) = backingList.removeAt(idx)
        indices.keys.forEach {
            val value = indices.getValue(it)
            if (value > idx)
                indices[it] = value - 1
        }
        return item
    }

    override fun setIndex(name: String, newIndex: Int): Boolean {
        val item = remove(name) ?: return false
        backingList.add(newIndex, name to item)
        indices.keys.forEach {
            val value = indices.getValue(it)
            if (value >= newIndex)
                indices[it] = value + 1
        }
        indices[name] = newIndex
        return true
    }

    override fun indexOf(name: String): Int {
        return indices[name] ?: -1
    }

    override fun swap(name1: String, name2: String): Boolean {
        if (name1 !in this || name2 !in this) return false
        val index1 = indexOf(name1)
        val index2 = indexOf(name2)
        setIndex(name2, index1)
        setIndex(name1, index2)
        return true
    }
}

class NamedListSerializer<T>(val itemSerializer: KSerializer<T>) : KSerializer<NamedList<T>> {
    private val mapSerializer = MapSerializer(String.serializer(), itemSerializer)
    override fun deserialize(decoder: Decoder): NamedList<T> {
        val map = decoder.decodeSerializableValue(mapSerializer)
        return NamedListImpl<T>(map.toList().toMutableList())
    }

    override val descriptor: SerialDescriptor = mapSerializer.descriptor

    override fun serialize(encoder: Encoder, value: NamedList<T>) {
        encoder.encodeSerializableValue(mapSerializer, value.toMap())
    }
}