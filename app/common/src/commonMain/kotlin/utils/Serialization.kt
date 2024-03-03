package com.rnett.spellbook.utils

import kotlinx.collections.immutable.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.SetSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.descriptors.setSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

typealias SerializableImmutableList<T> = @Serializable(ImmutableListSerializer::class) ImmutableList<T>
typealias SerializableImmutableSet<T> = @Serializable(ImmutableSetSerializer::class) ImmutableSet<T>
typealias SerializableImmutableMap<K, V> = @Serializable(ImmutableMapSerializer::class) ImmutableMap<K, V>

typealias SerializablePersistentList<T> = @Serializable(PersistentListSerializer::class) PersistentList<T>
typealias SerializablePersistentSet<T> = @Serializable(PersistentSetSerializer::class) PersistentSet<T>
typealias SerializablePersistentMap<K, V> = @Serializable(PersistentMapSerializer::class) PersistentMap<K, V>

class ImmutableListSerializer<T>(private val elementSerializer: KSerializer<T>) : KSerializer<ImmutableList<T>> {
    override val descriptor: SerialDescriptor = listSerialDescriptor(elementSerializer.descriptor)
    override fun serialize(encoder: Encoder, value: ImmutableList<T>) {
        return ListSerializer(elementSerializer).serialize(encoder, value.toList())
    }

    override fun deserialize(decoder: Decoder): ImmutableList<T> {
        return ListSerializer(elementSerializer).deserialize(decoder).toPersistentList()
    }
}

class ImmutableMapSerializer<K, V>(
    private val keySerializer: KSerializer<K>,
    private val valueSerializer: KSerializer<V>
) : KSerializer<ImmutableMap<K, V>> {
    override fun serialize(encoder: Encoder, value: ImmutableMap<K, V>) {
        return MapSerializer(keySerializer, valueSerializer).serialize(encoder, value)
    }

    override val descriptor: SerialDescriptor =
        mapSerialDescriptor(keySerializer.descriptor, valueSerializer.descriptor)

    override fun deserialize(decoder: Decoder): ImmutableMap<K, V> {
        return MapSerializer(keySerializer, valueSerializer).deserialize(decoder).toPersistentMap()
    }
}

class ImmutableSetSerializer<T>(private val elementSerializer: KSerializer<T>) : KSerializer<ImmutableSet<T>> {
    override val descriptor: SerialDescriptor = setSerialDescriptor(elementSerializer.descriptor)
    override fun serialize(encoder: Encoder, value: ImmutableSet<T>) {
        return SetSerializer(elementSerializer).serialize(encoder, value.toSet())
    }

    override fun deserialize(decoder: Decoder): ImmutableSet<T> {
        return SetSerializer(elementSerializer).deserialize(decoder).toPersistentSet()
    }
}

class PersistentListSerializer<T>(private val elementSerializer: KSerializer<T>) : KSerializer<PersistentList<T>> {
    override val descriptor: SerialDescriptor = listSerialDescriptor(elementSerializer.descriptor)
    override fun serialize(encoder: Encoder, value: PersistentList<T>) {
        return ListSerializer(elementSerializer).serialize(encoder, value.toList())
    }

    override fun deserialize(decoder: Decoder): PersistentList<T> {
        return ListSerializer(elementSerializer).deserialize(decoder).toPersistentList()
    }
}

class PersistentMapSerializer<K, V>(
    private val keySerializer: KSerializer<K>,
    private val valueSerializer: KSerializer<V>
) : KSerializer<PersistentMap<K, V>> {
    override fun serialize(encoder: Encoder, value: PersistentMap<K, V>) {
        return MapSerializer(keySerializer, valueSerializer).serialize(encoder, value)
    }

    override val descriptor: SerialDescriptor =
        mapSerialDescriptor(keySerializer.descriptor, valueSerializer.descriptor)

    override fun deserialize(decoder: Decoder): PersistentMap<K, V> {
        return MapSerializer(keySerializer, valueSerializer).deserialize(decoder).toPersistentMap()
    }
}

class PersistentSetSerializer<T>(private val elementSerializer: KSerializer<T>) : KSerializer<PersistentSet<T>> {
    override val descriptor: SerialDescriptor = setSerialDescriptor(elementSerializer.descriptor)
    override fun serialize(encoder: Encoder, value: PersistentSet<T>) {
        return SetSerializer(elementSerializer).serialize(encoder, value.toSet())
    }

    override fun deserialize(decoder: Decoder): PersistentSet<T> {
        return SetSerializer(elementSerializer).deserialize(decoder).toPersistentSet()
    }
}