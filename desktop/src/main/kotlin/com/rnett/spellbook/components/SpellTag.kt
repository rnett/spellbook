package com.rnett.spellbook.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.Composable
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
import com.rnett.spellbook.CastActionType
import com.rnett.spellbook.Color
import com.rnett.spellbook.Condition
import com.rnett.spellbook.Rarity
import com.rnett.spellbook.Save
import com.rnett.spellbook.School
import com.rnett.spellbook.SpellList
import com.rnett.spellbook.SpellType
import com.rnett.spellbook.TagColors
import com.rnett.spellbook.TargetingType
import com.rnett.spellbook.Trait
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.constantActionImg
import com.rnett.spellbook.eq

//TODO school and tag colors should be less bright

@Composable
fun SpellTag(
    content: String,
    color: Color,
    tooltip: String,
    modifier: Modifier = Modifier,
    sidebarInfo: SidebarData<*>? = null,
    noVerticalPadding: Boolean = false,
    textColor: Color? = null,
) =
    SpellTag(content, color.asCompose(), tooltip, modifier, sidebarInfo, noVerticalPadding, textColor?.asCompose())

@Composable
fun SpellTag(
    content: String,
    color: androidx.compose.ui.graphics.Color,
    tooltip: String,
    modifier: Modifier = Modifier,
    sidebarInfo: SidebarData<*>? = null,
    noVerticalPadding: Boolean = false,
    textColor: androidx.compose.ui.graphics.Color? = null,
) = SpellTag(color, tooltip, modifier, sidebarInfo, noVerticalPadding, textColor) {
    Text(content,
        color = textColor ?: androidx.compose.ui.graphics.Color.Unspecified)
}

@Composable
fun SpellTag(
    color: Color,
    tooltip: String,
    modifier: Modifier = Modifier,
    sidebarInfo: SidebarData<*>? = null,
    noVerticalPadding: Boolean = false,
    textColor: Color? = null,
    content: @Composable () -> Unit,
): Unit = SpellTag(color.asCompose(), tooltip, modifier, sidebarInfo, noVerticalPadding, textColor?.asCompose(), content)

@Composable
fun SpellTag(
    color: androidx.compose.ui.graphics.Color,
    tooltip: String,
    modifier: Modifier = Modifier,
    sidebarInfo: SidebarData<*>? = null,
    noVerticalPadding: Boolean = false,
    textColor: androidx.compose.ui.graphics.Color?,
    content: @Composable () -> Unit,
) {
    var myModifier = modifier

    if (sidebarInfo != null) {
        val sidebar = SidebarNav.currentSidebar()
        myModifier = myModifier.clickable(role = Role.Button, onClickLabel = "Open info sidebar") {
            sidebar(sidebarInfo)
        }
    }


    val vertPadding = if (noVerticalPadding) 0.dp else 3.dp

    Surface(shape = RoundedCornerShape(8.dp), color = color, contentColor = textColor ?: contentColorFor(color)) {
        Box(myModifier.padding(5.dp, vertPadding)) {
            ProvideTextStyle(TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)) {
                content()
            }
        }
    }
}

@Composable
fun TypeTag(type: SpellType) {
    SpellTag(TagColors.SpellType(type), type.name) {
        Text(type.name)
    }
}

//TODO flag manipulate (mini cross instead of *?)
@Composable
fun ActionsTag(actions: Actions) {
    val tooptip = when (actions) {
        is Actions.Constant -> if (actions.actions == 1) "1 Action" else "${actions.actions} Actions"
        is Actions.Variable -> "${actions.min} to ${actions.max} Actions"
        is Actions.Reaction -> "Reaction"
        is Actions.Time -> "Time"
        else -> error("Unknown actions $actions")
    } + if (actions.hasTrigger) ", With Trigger" else ""
    SpellTag(Color.Transparent, tooptip, noVerticalPadding = true) {
        Row {
            Row {
                when (actions) {
                    is Actions.Constant -> {
                        Icon(imageFromResource(constantActionImg(actions.actions)), actions.actions.toString())
                    }
                    is Actions.Variable -> {
                        Row {
                            Icon(imageFromResource(constantActionImg(actions.min)), actions.min.toString())
                            Icon(Icons.Default.ArrowForward, "to", Modifier.padding(horizontal = 3.dp))
                            Icon(imageFromResource(constantActionImg(actions.max)), actions.max.toString())
                        }
                    }
                    is Actions.Reaction -> {
                        Icon(imageFromResource("static/reaction.png"), "Reaction")
                    }
                    is Actions.Time -> {
                        Icon(imageFromResource("static/time.png"), "Duration")
                    }
                }
            }

            Spacer(Modifier.width(3.dp))

            if (actions.hasTrigger) {
                Row {
                    Text(
                        "*",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        style = LocalTextStyle.current.copy(baselineShift = BaselineShift.Superscript)
                    )
                }
            }
        }
    }
}

@Composable
fun ActionTypeTag(type: CastActionType) {
    SpellTag(type.name, TagColors.ActionType(type), type.name)
}

@Composable
fun SpellListTag(spellList: SpellList, placeholder: Boolean = false) =
    if (!placeholder)
        SpellTag(spellList.name, TagColors.SpellList(spellList), "Spell List: ${spellList.name}")
    else
        SpellTag(spellList.name, Color.Transparent, "Not in ${spellList.name}", textColor = Color.Transparent)

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

            Icon(imageFromResource("static/$name.png"), name, Modifier.height(18.dp).width(20.dp))
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
fun ConditionTag(condition: Condition, sidebar: Boolean = true) = SpellTag(
    condition.name,
    if (condition.positive == true) TagColors.Condition.Positive else TagColors.Condition.Negative,
    "Condition: ${condition.name} - ${condition.description}",
    sidebarInfo = if (sidebar) AonUrl("Conditions.aspx?ID=${condition.aonId}") else null
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
                    Icon(imageFromResource("static/sustained.png"), "Sustained", Modifier.height(20.dp))
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

                                Icon(imageFromResource("static/sustained.png"), "Sustained", Modifier.width(16.dp))
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
fun TraitTag(trait: Trait, specialTraits: Boolean = true, sidebar: Boolean = true) {
    val sidebarInfo = if (sidebar) AonUrl("Traits.aspx?ID=${trait.aonId}") else null
    if (specialTraits) {
        val (color, prefix) = when (trait) {
            in Rarity -> TagColors.Rarity(trait) to "Rarity"
            in School -> TagColors.School to "School"
            else -> if (trait eq Trait.Incapacitation)
                TagColors.Incapacitation to "Trait"
            else
                TagColors.Trait to "Trait"
        }
        SpellTag(trait.name, color.asCompose(), "$prefix: ${trait.name}", sidebarInfo = sidebarInfo)
    } else {
        SpellTag(trait.name, TagColors.Trait, "Trait: ${trait.name}", sidebarInfo = sidebarInfo)
    }
}