package com.rnett.spellbook.model.spellbook.dao

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.data.SpellbookDaoLoader
import com.rnett.spellbook.data.SpellbooksDao


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaoSelector(text: String, dao: SpellbooksDao?, select: (SpellbooksDao) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val daos = SpellbookDaoLoader.daos

        Text(text)

        Spacer(Modifier.width(3.dp))
        var dropdownExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(dropdownExpanded, { dropdownExpanded = it }) {
            TextField(
                value = dao?.name.orEmpty(),
                placeholder = { Text("Choose a data source") },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(dropdownExpanded, { dropdownExpanded = false }) {
                daos.forEach {
                    DropdownMenuItem(
                        { Text(it.name) },
                        {
                            dropdownExpanded = false
                            select(it)
                        },
                        leadingIcon = it.display.leadingIcon,
                        trailingIcon = it.display.trainingIcon,
                        contentPadding = PaddingValues(3.dp)
                    )
                }
            }
        }
    }
}