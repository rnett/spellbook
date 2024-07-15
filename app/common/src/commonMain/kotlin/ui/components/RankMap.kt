package com.rnett.spellbook.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.model.spellbook.RankMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap

@Composable
fun <T> DenseRankMap(
    rankMap: RankMap<T>,
    update: (RankMap<T>) -> Unit,
    item: @Composable (Int?, T?, (T?) -> Unit) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.width(30.dp), horizontalArrangement = Arrangement.Start) {
                Text("*")
            }

            item(RankMap.ALL_RANKS, rankMap[RankMap.ALL_RANKS]) {
                update(RankMap(rankMap.perRank.toPersistentMap().apply {
                    if (it == null)
                        remove(RankMap.ALL_RANKS)
                    else
                        put(RankMap.ALL_RANKS, it)
                }))
            }
        }
        val hasRanked by rememberUpdatedState(rankMap.keys.any { it != null })
        var individualRanks by remember { mutableStateOf(hasRanked) }

        Row(verticalAlignment = Alignment.CenterVertically) {
            LabeledCheckbox(individualRanks, { individualRanks = it }, enabled = !hasRanked, labelAfter = false) {
                Text("Individual ranks")
            }
            Spacer(Modifier.width(50.dp))
            IconButton({
                update(RankMap(rankMap.allRanks?.let { persistentMapOf(RankMap.ALL_RANKS to it) } ?: persistentMapOf()))
                individualRanks = false
            }) {
                Icon(Icons.Default.Delete, "Delete ranks")
            }
        }

        AnimatedVisibility(individualRanks) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                (RankMap.CANTRIPS..RankMap.MAX_RANK).forEach { rank ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Row(Modifier.width(30.dp), horizontalArrangement = Arrangement.Start) {
                            Text(rank.toString())
                        }
                        item(rank, rankMap[rank]) {
                            update(RankMap(rankMap.perRank.toPersistentMap().apply {
                                if (it == null)
                                    remove(rank)
                                else
                                    put(rank, it)
                            }))
                        }
                    }
                }
            }
        }
    }
}