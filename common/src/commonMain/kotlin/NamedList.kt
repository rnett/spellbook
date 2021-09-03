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
    private val backingList: MutableList<Pair<String, T>> = mutableListOf(),
    private val indices: MutableMap<String, Int> = LinkedHashMap(backingList.size)
) : MutableNamedList<T>,
    List<Pair<String, T>> by backingList {
compan
    

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
        indices.entries.forEach {
            if (it.value > idx)
                it.setValue(it.value - 1)
        }
        return item
    }

    override fun setIndex(name: String, newIndex: Int): Boolean {
        val item = remove(name) ?: return false
        backingList.add(newIndex, name to item)
        indices.entries.forEach {
            if (it.value >= newIndex)
                it.setValue(it.value + 1)
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