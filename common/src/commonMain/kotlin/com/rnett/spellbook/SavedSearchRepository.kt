package com.rnett.spellbook

import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.spellbook.Spellbook
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class NamedObject<T>(val name: String, val value: T)

abstract class NamedObjectRepository<T>(
    initial: Map<String, T>,
    baseName: String,
) {
    open fun suffix(i: Int): String = " $i"
    protected open fun onUpdate(value: PersistentMap<String, T>) {

    }

    private val mutableState: MutableStateFlow<NamedObjectView<T>> =
        MutableStateFlow(NamedObjectView(initial.toPersistentMap(), baseName, ::suffix))

    var value: NamedObjectView<T>
        get() = mutableState.value
        set(value) {
            onUpdate(value.all)
            mutableState.value = value
        }

    val state: StateFlow<NamedObjectView<T>> = mutableState
}

data class NamedObjectView<T>(
    val all: PersistentMap<String, T>,
    private val baseName: String,
    private val suffix: (Int) -> String
) :
    Map<String, T> by all {

    fun replace(key: String, value: T) = copy(all = all.put(key, value))

    fun remove(key: String) = copy(all = all.remove(key))

    fun newName(): String {
        var name = baseName
        var i = 0
        while (name in this) {
            i++
            name = baseName + suffix(i)
        }
        return name
    }

    fun rename(old: String, new: String) = copy(all = all.keys.toPersistentList().mutate {
        it[it.indexOf(old)] = new
    }.associateWith {
        all.getValue(
            if (it == new)
                old
            else
                it
        )
    }.toPersistentMap()
    )

    fun add(value: T) = replace(newName(), value)
}

typealias SavedSearchs = NamedObjectView<SpellFilter>

typealias Spellbooks = NamedObjectView<Spellbook>