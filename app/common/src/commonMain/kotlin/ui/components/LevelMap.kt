package com.rnett.spellbook.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.model.spellbook.LevelMap
import kotlinx.collections.immutable.toPersistentMap

@Composable
private fun <T> LevelItem(level: Int?, levelMap: LevelMap<T>, perLevel: @Composable (Int?, T?) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(if (level == LevelMap.ALL_LEVELS) "All" else "Level $level")
            Spacer(Modifier.width(40.dp))
            HorizontalDivider()
        }
        perLevel(level, levelMap[level])
    }
}

@Composable
fun <T> LevelMap(levelMap: LevelMap<T>, perLevel: @Composable (Int?, T?) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        LevelItem(LevelMap.ALL_LEVELS, levelMap, perLevel)

        (LevelMap.MIN_LEVEL..LevelMap.MAX_LEVEL).forEach {
            LevelItem(it, levelMap, perLevel)
        }
    }
}

@Composable
fun <T> SparseLevelMap(levelMap: LevelMap<T>, perLevel: @Composable (Int?, T?) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        LevelItem(LevelMap.ALL_LEVELS, levelMap, perLevel)

        levelMap.keys.forEach {
            LevelItem(it, levelMap, perLevel)
        }
    }
}

@Composable
fun <T> SparseLevelMapForm(
    levelMap: LevelMap<T>,
    update: (LevelMap<T>) -> Unit,
    createNew: () -> T,
    showAll: Boolean = false,
    perLevel: @Composable (Int?, T?, (T?) -> Unit) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(Modifier.width(20.dp))
            Text("All levels")
            HorizontalDivider(Modifier.weight(1f))
        }
        Row(Modifier.padding(5.dp)) {
            perLevel(LevelMap.ALL_LEVELS, levelMap.allLevels) {
                update(LevelMap(levelMap.atLevel.toPersistentMap().apply {
                    if (it == null)
                        remove(LevelMap.ALL_LEVELS)
                    else
                        put(LevelMap.ALL_LEVELS, it)
                }))
            }
        }
        val indices = if (showAll) (LevelMap.MIN_LEVEL..LevelMap.MAX_LEVEL) else levelMap.keys

        indices.forEach { level ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HorizontalDivider(Modifier.width(20.dp))
                Text("Level $level")
                HorizontalDivider(Modifier.weight(1f))
                IconButton({
                    update(LevelMap(levelMap.atLevel.toPersistentMap().remove(level)))
                }, Modifier.size(20.dp)) {
                    Icon(Icons.Default.Remove, "Remove")
                }
                HorizontalDivider(Modifier.width(20.dp))
            }
            Row(Modifier.padding(5.dp)) {
                perLevel(level, levelMap[level]) {
                    update(LevelMap(levelMap.atLevel.toPersistentMap().apply {
                        if (it == null)
                            remove(level)
                        else
                            put(level, it)
                    }))
                }
            }
        }
        if (!showAll) {
            HorizontalDivider()
            Row(
                Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                var newLevel by remember { mutableStateOf<Int?>(null) }
                Text("Add level")

                SmallIntField(
                    newLevel, { newLevel = it },
                    minimum = LevelMap.MIN_LEVEL,
                    maximum = LevelMap.MAX_LEVEL,
                )

                IconButton({
                    val level = newLevel ?: return@IconButton
                    if (level !in levelMap && level > LevelMap.MIN_LEVEL && level < LevelMap.MAX_LEVEL) {
                        update(LevelMap(levelMap.atLevel.toPersistentMap().put(level, createNew())))
                    }
                }) {
                    Icon(Icons.Default.Add, "Add")
                }

            }
        }
    }
}