package com.rnett.spellbook

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = NamedListSerializer::class)
@Polymorphic
interface NamedList<out T> : List<Pair<String, T>> {
    operator fun get(name: String): T?
    operator fun contains(name: String): Boolean
    fun newName(baseName: String, suffix: (Int) -> String): String

    fun indexOf(name: String): Int

    val keys: List<String>
    val values: List<T>
}

inline fun <T, R, D : MutableNamedList<R>> NamedList<T>.mapValuesTo(dest: D, transform: (String, T) -> R): D {
    forEach { (k, v) ->
        dest[k] = transform(k, v)
    }
    return dest
}

inline fun <T, R> NamedList<T>.mapValues(transform: (String, T) -> R): NamedList<R> =
    mapValuesTo(mutableNamedListOf(), transform)

private object EmptyNamedList : NamedList<Nothing>, List<Pair<String, Nothing>> by emptyList() {
    override fun get(name: String): Nothing? = null

    override fun contains(name: String): Boolean = false

    override fun newName(baseName: String, suffix: (Int) -> String): String = baseName

    override fun indexOf(name: String): Int = -1

    override val keys: List<String> = emptyList()
    override val values: List<Nothing> = emptyList()
}

fun <T> emptyNamedList(): NamedList<T> = EmptyNamedList

interface MutableNamedList<T> : NamedList<T> {
    operator fun set(name: String, value: T): T?
    fun setAll(items: Map<String, T>)
    fun setIndex(name: String, newIndex: Int): Boolean

    fun remove(name: String): T?

    fun rename(oldName: String, newName: String): Boolean

    fun swap(name1: String, name2: String): Boolean
}

fun <T> namedListOf(vararg items: Pair<String, T>): NamedList<T> = NamedListImpl(items.toMutableList())

fun <T> mutableNamedListOf(vararg items: Pair<String, T>): MutableNamedList<T> = NamedListImpl(items.toMutableList())

fun <T> NamedList<T>.toMutableNamedList(): MutableNamedList<T> = mutableNamedListOf<T>(*toTypedArray())

open class NamedListImpl<T>(
    @Suppress("UNCHECKED_CAST") private val backingList: MutableList<Pair<String, T>> = mutableListOf(),
    private val indices: MutableMap<String, Int> = LinkedHashMap(backingList.size)
) : MutableNamedList<T>,
    List<Pair<String, T>> by backingList {


    init {
        indices.apply {
            backingList.forEachIndexed { idx, (name, _) ->
                this[name] = idx
            }
        }
    }

    override val keys: List<String>
        get() = backingList.map { it.first }

    override val values: List<T>
        get() = backingList.map { it.second }

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

    override fun equals(other: Any?): Boolean {
        if (other !is NamedList<*>) return false
        if (size != other.size) return false

        for (i in 0 until size) {
            if (this[i] != other[i]) return false
        }

        return true
    }

    override fun hashCode(): Int {
        return backingList.hashCode()
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