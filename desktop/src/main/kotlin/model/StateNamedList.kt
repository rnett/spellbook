package com.rnett.spellbook.model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.rnett.spellbook.NamedList
import com.rnett.spellbook.NamedListImpl

fun <T> mutableStateNamedListOf(vararg items: Pair<String, T>) = StateNamedList(mutableStateListOf(*items))

class StateNamedList<T>(
    backingList: SnapshotStateList<Pair<String, T>> = mutableStateListOf(),
    indices: MutableMap<String, Int> = mutableStateMapOf()
) :
    NamedListImpl<T>(backingList, indices)

fun <T> NamedList<T>.toStateNamedList() = mutableStateNamedListOf(*toTypedArray())