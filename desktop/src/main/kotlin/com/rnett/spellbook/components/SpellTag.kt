package com.rnett.spellbook.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AmbientTextStyle
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rnett.spellbook.Actions
import com.rnett.spellbook.Color
import com.rnett.spellbook.Condition
import com.rnett.spellbook.Rarity
import com.rnett.spellbook.Save
import com.rnett.spellbook.School
import com.rnett.spellbook.SpellList
import com.rnett.spellbook.TagColors
import com.rnett.spellbook.TargetingType
import com.rnett.spellbook.Trait
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.constantActionImg

@Composable
fun SpellTag(
    content: String,
    color: Color,
    tooltip: String,
    modifier: Modifier = Modifier,
    sidebarInfo: SidebarData<*>? = null,
    noVerticalPadding: Boolean = false,
) =
    SpellTag(color.asCompose(), tooltip, modifier, sidebarInfo, noVerticalPadding) { Text(content) }

@Composable
fun SpellTag(
    content: String,
    color: androidx.compose.ui.graphics.Color,
    tooltip: String,
    modifier: Modifier = Modifier,
    sidebarInfo: SidebarData<*>? = null,
    noVerticalPadding: Boolean = false,
) = SpellTag(color, tooltip, modifier, sidebarInfo, noVerticalPadding) { Text(content) }

@Composable
fun SpellTag(
    color: Color,
    tooltip: String,
    modifier: Modifier = Modifier,
    sidebarInfo: SidebarData<*>? = null,
    noVerticalPadding: Boolean = false,
    content: @Composable () -> Unit
): Unit = SpellTag(color.asCompose(), tooltip, modifier, sidebarInfo, noVerticalPadding, content)

@Composable
fun SpellTag(
    color: androidx.compose.ui.graphics.Color,
    tooltip: String,
    modifier: Modifier = Modifier,
    sidebarInfo: SidebarData<*>? = null,
    noVerticalPadding: Boolean = false,
    content: @Composable () -> Unit
) {
    var myModifier = modifier

    if (sidebarInfo != null) {
        val sidebar = SidebarNav.currentSidebar
        myModifier = myModifier.clickable(role = Role.Button, onClickLabel = "Open info sidebar") {
            sidebar(sidebarInfo)
        }
    }


    val vertPadding = if (noVerticalPadding) 0.dp else 3.dp

    Surface(shape = RoundedCornerShape(8.dp), color = color) {
        Box(myModifier.padding(5.dp, vertPadding)) {
            Providers(AmbientTextStyle provides TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)) {
                content()
            }
        }
    }
}

@Composable
fun ActionsTag(actions: Actions) {
    val tooptip = when (actions) {
        is Actions.Constant -> if (actions.actions == 1) "1 Action" else "${actions.actions} Actions"
        is Actions.Variable -> "${actions.min} to ${actions.max} Actions"
        is Actions.Reaction -> "Reaction"
        is Actions.Time -> "Time"
    } + if (actions.hasTrigger) ", With Trigger" else ""
    SpellTag(Color.Transparent, tooptip, noVerticalPadding = true) {
        Row {
            Row {
                when (actions) {
                    is Actions.Constant -> {
                        Icon(imageFromResource(constantActionImg(actions.actions)))
                    }
                    is Actions.Variable -> {
                        Row {
                            Icon(imageFromResource(constantActionImg(actions.min)))
                            Icon(Icons.Default.ArrowForward, Modifier.padding(horizontal = 3.dp))
                            Icon(imageFromResource(constantActionImg(actions.max)))
                        }
                    }
                    is Actions.Reaction -> {
                        Icon(imageFromResource("static/reaction.png"))
                    }
                    is Actions.Time -> {
                        Icon(imageFromResource("static/time.png"))
                    }
                }
            }

            Spacer(Modifier.preferredWidth(3.dp))

            if (actions.hasTrigger) {
                Row {
                    Text(
                        "*",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        style = AmbientTextStyle.current.copy(baselineShift = BaselineShift.Superscript)
                    )
                }
            }
        }
    }
}

@Composable
fun SpellListTag(spellList: SpellList) = SpellTag(spellList.name, TagColors.SpellList(spellList), "Spell List: ${spellList.name}")

@Composable
fun TargetingTag(targeting: TargetingType) = SpellTag(TagColors.Targeting(targeting), "Targeting: $targeting") {
    when (targeting) {
        TargetingType.Other -> {
            Text("Other")
        }
        TargetingType.Area.Other -> {
            Text("Area")
        }
        else -> {
            val name = when (targeting) {
                TargetingType.SingleTarget -> "target"
                TargetingType.MultiTarget -> "multitarget"
                TargetingType.Area.Cone -> "cone"
                TargetingType.Area.Line -> "line"
                TargetingType.Area.Emanation -> "emanation"
                TargetingType.Area.Burst -> "burst"
                TargetingType.Area.Wall -> "wall"
                else -> error("Impossible targeting type: $targeting")
            }

            Icon(imageFromResource("static/$name.png"), Modifier.preferredHeight(18.dp).preferredWidth(20.dp))
        }
    }
}

@Composable
fun AttackTag() = SpellTag("Attack", TagColors.Attack.Attack, "Spell Attack")

@Composable
fun SaveTag(save: Save, isBasicSave: Boolean) {
    val name = save.name + if (isBasicSave) "*" else ""
    val tooltip = "Save: " + (if (isBasicSave) "Basic " else "") + save.name
    SpellTag(name, TagColors.Attack.Save(save), tooltip)
}

@Composable
fun ConditionTag(condition: Condition) = SpellTag(
    condition.name,
    if (condition.positive == true) TagColors.Condition.Positive else TagColors.Condition.Negative,
    "Condition: ${condition.name} - ${condition.description}",
    sidebarInfo = AonUrl("Conditions.aspx?ID=${condition.aonId}")
)

@Composable
fun DurationTag(duration: String?, isSustained: Boolean) {
    if (duration == null) {
        SpellTag("Instant", TagColors.Duration.Instant, "Duration: Instant")
    } else {
        val duration = duration.trim().capitalize()

        if (isSustained) {
            if (duration == "Sustained") {
                SpellTag(TagColors.Duration.Sustained, "Duration: Sustained") {
                    Icon(imageFromResource("static/sustained.png"), Modifier.preferredHeight(20.dp))
                }
            } else {
                SpellTag(TagColors.Duration.Sustained, "Duration: $duration") {
                    val times = mutableListOf<String>()
                    val text = Regex("sustained (for )?up to ([\\w ]+)", RegexOption.IGNORE_CASE).replace(duration) {
                        times += it.groupValues[2]
                        "\$\$||\$\$"
                    }

                    val parts = text.split("\$\$")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        parts.forEachIndexed { idx, it ->
                            if (it == "||") {

                                Icon(imageFromResource("static/sustained.png"), Modifier.preferredHeight(16.dp))
                                Text("(${times.removeFirst()})")
                            } else
                                Text(it)
                        }
                    }
                }
            }
        } else {
            SpellTag(duration, TagColors.Duration.NonSustained, "Duration: $duration")
        }
    }
}

@Composable
fun TraitTag(trait: Trait, specialTraits: Boolean = true) {
    val sidebarInfo = AonUrl("Traits.aspx?ID=${trait.aonId}")
    if (specialTraits) {
        val (color, prefix) = when (trait) {
            in Rarity -> TagColors.Rarity(trait) to "Rarity"
            in School -> TagColors.School to "School"
            else -> TagColors.Trait to "Trait"
        }
        SpellTag(trait.name, color, "$prefix: ${trait.name}", sidebarInfo = sidebarInfo)
    } else {
        SpellTag(trait.name, TagColors.Trait, "Trait: ${trait.name}", sidebarInfo = sidebarInfo)
    }
}