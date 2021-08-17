package com.rnett.spellbook.load

import com.rnett.spellbook.db.Conditions
import com.rnett.spellbook.db.DbCondition
import com.rnett.spellbook.db.DbSpell
import com.rnett.spellbook.db.DbTrait
import com.rnett.spellbook.db.SpellConditions
import com.rnett.spellbook.db.SpellLists
import com.rnett.spellbook.db.SpellTraits
import com.rnett.spellbook.db.SpellbookDB
import com.rnett.spellbook.db.Spells
import com.rnett.spellbook.db.Traits
import com.rnett.spellbook.invoke
import com.rnett.spellbook.spell.Actions
import com.rnett.spellbook.spell.CastActionType
import com.rnett.spellbook.spell.Condition
import com.rnett.spellbook.spell.Creature
import com.rnett.spellbook.spell.Heightening
import com.rnett.spellbook.spell.Rarity
import com.rnett.spellbook.spell.Save
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spell.SpellType
import com.rnett.spellbook.spell.Summons
import com.rnett.spellbook.spell.Trait
import com.rnett.spellbook.spell.TraitKey
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.tongfei.progressbar.ProgressBar
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

val client = HttpClient() {
}

suspend fun loadPage(url: String): Document {
    return Jsoup.parse(client.get<String>(url)).also {
        it.setBaseUri(url)
    }
}

class SpellData(val spell: Spell, val conditionNames: Set<String>, val traits: Set<TraitKey>)

fun parse(doc: Document, conditions: Set<String>, seenSpells: Set<String>): SpellData? {
    val name = doc.select("h1.title").textNodes().single().text()

    if (name in seenSpells)
        return null

    try {
        val levelStr = doc.select("h1.title > span").text()

        val spoilerRegex = Regex("This Spell is from the ([\\w ]+) and may contain Spoilers")
        val spoilerWarnings =
            doc.select("#ctl00_MainContent_DetailedOutput h2.title").filter { it.text().matches(spoilerRegex) }
        assert(spoilerWarnings.size <= 1) { "More than one spoiler warning" }
        val spoilers = spoilerWarnings.singleOrNull()?.let { spoilerRegex.matchEntire(it.text())!!.groupValues[1] }

        val statblock = doc.select("#ctl00_MainContent_DetailedOutput").single().childNode(0)
            .siblingsUntilElement { it.normalTagName == "hr" }

        spoilerWarnings.forEach { it.remove() }

        val otherHs = doc.select("#ctl00_MainContent_DetailedOutput > h2.title")

        val postfix = otherHs.firstOrNull()?.siblingsUntil { false }?.let { listOf(otherHs.first()) + it }
        postfix?.forEach { it.remove() }

        val fullText = doc.select("#ctl00_MainContent_DetailedOutput").text()
        val fullTextLower = fullText.lowercase(Locale.getDefault())

        val type = when {
            "Cantrip" in levelStr -> SpellType.Cantrip
            "Focus" in levelStr -> SpellType.Focus
            else -> SpellType.Spell
        }

        val level = levelStr.substringAfter(" ").toInt()

        val aonId = doc.baseUri().substringAfter("ID=").toInt()

        val lists = doc.select("#ctl00_MainContent_DetailedOutput > u > a")
            .filter { it.attr("href").startsWith("Spells.aspx?Tradition=") }
            .map { SpellList(it.text()) }.let {
                if (it.isEmpty())
                    listOf(SpellList.Focus)
                else
                    it
            }

        val traits = doc.select("#ctl00_MainContent_DetailedOutput > span")
            .filter { it.classNames().any { it.startsWith("trait") } }
            .map { TraitKey(it.text()) }
            .toMutableSet()

        if (traits.none { it in Rarity }) {
            traits += Rarity.Common
        }

        var basicSave: Boolean = false
        var save: Save? = null

        //TODO handle multiple saves for extra effects
        Save.values().forEach {
            if ("Saving Throw basic $it" in fullText) {
                basicSave = true
                save = it
            } else if ("basic ${it.name.lowercase(Locale.getDefault())} save" in fullTextLower) {
                basicSave = true
                save = it
            } else if ("Saving Throw $it" in fullText) {
                basicSave = false
                save = it
            } else if ("${it.name.lowercase(Locale.getDefault())} save" in fullTextLower) {
                if (save == null) {
                    basicSave = false
                    save = it
                }
            }
        }

        val Bs = statblock.elements().filter { it.normalTagName == "b" }

        val requiresAttackRoll =
            "spell attack" in fullTextLower || "spell attack roll" in fullTextLower || traits.any { it == Trait.Attack }

        val sourceElement = Bs.single { it.text() == "Source" }.nextElementSibling()
        assert(sourceElement.attr("href").startsWith("https://paizo.com")) { "Didn't find source?" }
        val source = sourceElement.text()

        val trigger = Bs.singleOrNull { it.text() == "Trigger" }?.siblingsToBreak()?.textWithNewlines()

        //TODO handle cost
        val actionsElements = Bs.single { it.text() == "Cast" }.siblingsToBreak().takeWhile {
            !(it is Element && (it.text() == "Requirements" || it.text() == "Cost" || it.normalTagName == "b"))
        }
        val actionValues = actionsElements.filterIsInstance<Element>().filter { it.className() == "actiondark" }
            .map { actionNumForText(it.attr("alt")) }

        val actionsText = (
                if (actionsElements.filterIsInstance<Element>().count { it.className() == "actiondark" } > 1)
                    actionsElements.takeLastWhile { it !is Element || !it.className().startsWith("action") }
                else
                    actionsElements).textWithNewlines(false).replace("\n", " ").trim(' ', ';').ifBlank { null }

        var actionTypes: List<CastActionType>? =
            if (!actionValues.isEmpty() || (actionsText != null && '(' in actionsText && ')' in actionsText))
                actionsText?.substringAfter("(")?.substringBefore(")")?.split(",", " or ")
                    ?.map { CastActionType.valueOf(it.trim().enumFormat()) }
            else
                null

        val actions: Actions = when {
            actionValues.isEmpty() -> {
                actionsText!!
                Actions.Time(actionsText.substringBefore("(").trim(), trigger)
            }
            actionValues.size > 1 -> {
                assert(actionValues.size == 2) { "More than 2 action values" }
                assert(actionValues.all { it > 0 }) { "Variable actions with free actions or reactions" }

                Actions.Variable(actionValues[0], actionValues[1], trigger)
            }
            else -> {
                when (val action = actionValues[0]) {
                    -1 -> {
                        actionTypes = null
                        Actions.Reaction(trigger)
                    }
                    else -> {
                        if (actionsText != null && "or more" in actionsText)
                            Actions.Variable(action, 3, trigger)
                        else
                            Actions.Constant(action, trigger)

                    }
                }
            }
        }

        val requirements =
            Bs.singleOrNull { it.text() == "Requirements" }?.elementSiblingsToBreak()?.textWithNewlines()
                ?.trim(' ', ';')

        val range =
            Bs.singleOrNull { it.text() == "Range" }?.nextSibling()?.safeAs<TextNode?>()?.text()?.trim(' ', ';')

        val targets =
            Bs.singleOrNull { it.text() == "Targets" }?.nextSibling()?.safeAs<TextNode?>()?.text()?.trim(' ', ';')

        val duration =
            Bs.singleOrNull { it.text() == "Duration" }?.nextSibling()?.safeAs<TextNode?>()?.text()?.trim(' ', ';')

        val area =
            Bs.singleOrNull { it.text() == "Area" }?.nextSibling()?.safeAs<TextNode?>()?.text()?.trim(' ', ';')

        val sustained = duration?.let { "sustained" in it.lowercase(Locale.getDefault()) } == true

        val hrs = doc.select("#ctl00_MainContent_DetailedOutput > hr").toList()

        assert(hrs.isNotEmpty()) { "Didn't find any <hr>s?!?!?" }

        val description = adjustAonHtml(
            doc.select("#ctl00_MainContent_DetailedOutput > hr").first()
                .siblingsUntilElement { it.normalTagName == "hr" }
                .map { if (it is Element) adjustAonElement(it) else it }
                .map { it.outerHtml() }
                .joinToString("\n")
        )

        val heightening: Heightening?
        if (hrs.size > 1) {
            val start = hrs[1]
            val first = start.nextElementSibling().text().substringAfter('(').substringBefore(')')
            if ('+' in first) {
                val increment = first.substringAfter('+').toInt()
                val effect = (start.nextElementSibling().nextSibling() as? TextNode
                    ?: error("Heighten description wasn't text")).text()
                heightening = Heightening.Every(increment, effect.trim())
            } else {
                val levels = start.nextElementSiblings()
                    .filter { it.tag().normalName() == "b" && it.text().startsWith("Heightened") }
                heightening = Heightening.Specific(
                    levels.associate {
                        val heighteningLevel = Regex("(\\d+)\\D\\D").find(
                            it.text().substringAfter('(').substringBefore(')')
                        )!!.groupValues[1].toInt()
                        val effect =
                            (it.nextSibling() as? TextNode
                                ?: error("Heighten description wasn't text")).text()
                        heighteningLevel to effect.trim()
                    }
                )
            }
        } else {
            heightening = null
        }

        val postfixStr: String?
        val summons: Summons?

        if (postfix != null) {
            fun makePostFixStr() =
                adjustAonHtml(postfix.map { if (it is Element) adjustAonElement(it) else it }
                    .joinToString("\n") { it.outerHtml() })

            val titles = postfix.elements().filter { it.normalTagName == "h2" }
            val regex = Regex("Level ([0-9\\-]+) \\D+")
            val matches = titles.mapNotNull { regex.matchEntire(it.text()) }
            if (matches.size == titles.size) {
                // summon spell
                summons = matches.mapIndexed { idx, it ->
                    val element = titles[idx]
                    val creatureLevel = it.groupValues[1].toInt()

                    val nextTitle = titles.getOrNull(idx + 1)?.let { postfix.indexOf(it) }
                        ?: postfix.size
                    val creatures = postfix.subList(postfix.indexOf(element), nextTitle).elements()
                        .map { it.select("a") }.filter { it.isNotEmpty() }
                        .map { Creature(it.text(), it.attr("href").substringAfter("Monsters.aspx?ID=").toInt()) }

                    creatureLevel to creatures
                }.let {
                    Summons.Multiple(it.toMap())
                }
                postfixStr = null
            } else {

                // inline summon, i.e. https://2e.aonprd.com/Spells.aspx?ID=352
                if (titles.size == 1) {
                    val title = titles[0]
                    val link = title.select("a").singleOrNull()
                    if (link != null && link.attr("href").startsWith("Monsters.aspx?ID=")) {
                        summons = Summons.Single(
                            Creature(
                                link.text(),
                                link.attr("href").substringAfter("Monsters.aspx?ID=").toInt()
                            )
                        )
                        postfixStr = null
                    } else {
                        summons = null
                        postfixStr = makePostFixStr()
                    }
                } else {
                    summons = null
                    postfixStr = makePostFixStr()
                }
            }
        } else {
            postfixStr = null
            summons = null
        }

        val spellConditions = conditions.filter {
            Regex("\\W$it\\W", RegexOption.IGNORE_CASE).containsMatchIn(description)
        }.toSet()

        return SpellData(
            Spell(
                name,
                level,
                aonId,
                type,
                lists.toSet(),
                emptySet(),
                save,
                basicSave,
                requiresAttackRoll,
                source,
                actions,
                actionTypes,
                requirements,
                range,
                targets,
                duration,
                sustained,
                area,
                description,
                heightening,
                summons,
                postfixStr,
                spoilers,
                emptySet()
            ),
            spellConditions,
            traits.toSet()
        )
    } catch (e: Throwable) {
        System.err.println("Error on spell: $name @ ${doc.baseUri()}")
        throw e
    }
}

suspend fun doInserts(spells: List<SpellData>) {
    newSuspendedTransaction {

        spells.forEach { spell ->
            val it = spell.spell
            DbSpell.new(it.name) {
                this.level = it.level
                this.aonId = it.aonId
                this.type = it.type
                this.save = it.save
                this.basicSave = it.basicSave
                this.requiresAttackRoll = it.requiresAttackRoll
                this.source = it.source
                this.actions = it.actions
                this.actionTypes = it.actionTypes
                this.requirements = it.requirements
                this.range = it.range
                this.targets = it.targets
                this.duration = it.duration
                this.sustained = it.sustained
                this.area = it.area
                this.description = it.description
                this.heightening = it.heightening
                this.summons = it.summons
                this.postfix = it.postfix
                this.spoilers = it.spoilersFor

                setSpecialTraits(spell.traits)
            }
        }
    }

    newSuspendedTransaction {
        SpellLists.batchInsert(
            spells.flatMap { spell -> spell.spell.lists.map { spell.spell.name to it } },
            shouldReturnGeneratedValues = false
        ) { (spell, list) ->
            this[SpellLists.spell] = spell
            this[SpellLists.spellList] = list
        }

        SpellTraits.batchInsert(
            spells.flatMap { spell -> spell.traits.map { spell.spell.name to it.name } },
            shouldReturnGeneratedValues = false
        ) { (spell, trait) ->
            this[SpellTraits.spell] = spell
            this[SpellTraits.trait] = trait
        }
        SpellConditions.batchInsert(
            spells.flatMap { spell -> spell.conditionNames.map { spell.spell.name to it } },
            shouldReturnGeneratedValues = false
        ) {
            this[SpellConditions.spell] = it.first
            this[SpellConditions.condition] = it.second
        }
    }
}

suspend fun loadConditions(url: String = "https://2e.aonprd.com/Conditions.aspx"): Set<Condition> {
    val doc = loadPage(url)
    val headers = doc.select("#ctl00_MainContent_DetailedOutput > h2.title")

    return headers.map {
        val name = it.text()
        val id = it.select("a").attr("href").substringAfter("Conditions.aspx?ID=").toInt()
        val source = it.nextElementSibling().let {
            check(it.text() == "Source")
            it.nextElementSibling().text()
        }

        val description =
            it.nextElementSibling().nextElementSibling().nextElementSibling().nextElementSibling()
                .siblingsUntilElement { it.normalTagName == "h2" }
                .textWithNewlines()
        Condition(name, source, description, id, null)
    }.toSet()
}

suspend fun insertConditions(conditions: Iterable<Condition>) {
    newSuspendedTransaction {
        val newConditions = conditions.filter { DbCondition.findById(it.name) == null }
        val oldConditions = conditions - newConditions
        Conditions.batchInsert(newConditions, shouldReturnGeneratedValues = false) {
            this[Conditions.id] = it.name
            this[Conditions.conditionSource] = it.source
            this[Conditions.description] = it.description
            this[Conditions.aonId] = it.aonId
            this[Conditions.positive] = it.positive
        }
        oldConditions.forEach {
            DbCondition[it.name].apply {
                this.source = it.source
                this.description = it.description
                this.aonId = it.aonId
                if (it.positive != null)
                    this.positive = positive
            }
        }
    }
}

suspend fun loadTrait(url: String): Trait {
    val doc = loadPage(url)
    val name = doc.select("#ctl00_MainContent_DetailedOutput > h1 > a").text()
    val aonId = url.substringAfter("?ID=").toInt()

    val body = doc.select("#ctl00_MainContent_DetailedOutput").single()

    val descStart = body.childNodes().first { it is TextNode }

    val description = descStart.siblingsUntilElement { it.normalTagName == "h2" }.textWithNewlines()
    return Trait(name, aonId, description)
}

@OptIn(FlowPreview::class)
suspend fun loadTraits(url: String = "https://2e.aonprd.com/Traits.aspx"): Set<Trait> {
    val doc = loadPage(url)
    val urls = doc.select("#ctl00_MainContent_DetailedOutput > span.trait > a").map {
        "https://2e.aonprd.com/" + it.attr("href")
    }

    return urls.asFlow().flatMapMerge(100) { flowOf(loadTrait(it)) }.flowOn(Dispatchers.IO).toList().toSet()
}

suspend fun insertTraits(traits: Iterable<Trait>) {
    newSuspendedTransaction {
        val newTraits = traits.filter { DbTrait.findById(it.name) == null }
        val oldTraits = traits - newTraits
        Traits.batchInsert(newTraits, shouldReturnGeneratedValues = true) {
            this[Traits.id] = it.name
            this[Traits.description] = it.description
            this[Traits.aonId] = it.aonId
        }
        oldTraits.forEach {
            DbTrait[it.name].apply {
                this.description = it.description
                this.aonId = it.aonId
            }
        }
    }
}

suspend fun spellsFromPage(url: String) = loadPage(url).select("a")
    .map { it.attr("href") }
    .filter { it.startsWith("Spells.aspx?ID=") }
    .map { "https://2e.aonprd.com/$it" }

//TODO track conditions
@OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)
suspend fun loadSpells(
    spells: Collection<String>,
    conditions: Set<String>,
    bufferPages: Int = 500,
    batchUpdates: Int = 50
) {
//    val spellBar = Mutexed(ProgressBar("Spells", spells.size.toLong()))
    val pagesBar = Mutexed(ProgressBar("Spells", spells.size.toLong()))

    val spellsDone = AtomicInteger(0)

    suspend fun updateSpells(n: Int) {
        pagesBar.withLock { it.extraMessage = "Added to DB: ${spellsDone.addAndGet(n)}" }
    }

    val inserts = mutableListOf<Job>()
    val foundSpells = Mutexed(mutableListOf<SpellData>())
    val seenSpells =
        newSuspendedTransaction { Spells.slice(Spells.id).selectAll().map { it[Spells.id].value }.toMutableSet() }

    coroutineScope {
        val spellPages = spells.asFlow().flatMapMerge(200) { flowOf(loadPage(it)) }
            .flowOn(Dispatchers.IO)
            .onEach {
                pagesBar.withLock { pages ->
                    pages.step()
                }

            }
            .buffer(bufferPages)

        spellPages.collect { doc ->

            val spell = parse(doc, conditions, seenSpells)
            if (spell != null) {
                seenSpells += spell.spell.name
                foundSpells.withLock { foundSpells ->
                    foundSpells += spell
                    if (foundSpells.size >= batchUpdates) {
                        val toInsert = foundSpells.toList()
                        foundSpells.clear()
                        inserts += launch(Dispatchers.IO) {
                            doInserts(toInsert)
                            updateSpells(toInsert.size)
                        }
                    }
                }
            } else
                updateSpells(1)
        }
        foundSpells.withLock { foundSpells ->
            val toInsert = foundSpells.toList()
            foundSpells.clear()
            inserts += launch(Dispatchers.IO) {
                doInserts(toInsert)
                updateSpells(toInsert.size)
            }
        }

        inserts.joinAll()
//        spellBar.withLock { it.close() }
        pagesBar.withLock { it.close() }
    }
}

fun main(args: Array<String>): Unit = runBlocking {
    if (args.isEmpty() || args[0].lowercase(Locale.getDefault()).let { it != "pg" && it != "postgres" }) {
        println("Loading to H2")
        SpellbookDB.initH2()
    } else {
        println("Loading to Postgres")
        SpellbookDB.initPostgres()
    }

    SpellbookDB.initTables()
    loggedTransaction {
        Spells.deleteAll()
    }

    val conditions = loadConditions()
    insertConditions(conditions)

    val traits = loadTraits()
    insertTraits(traits)

//    parse(loadPage("https://2e.aonprd.com/Spells.aspx?ID=508"), emptySet())
    val pages = listOf(
        "https://2e.aonprd.com/Spells.aspx?Tradition=1",
        "https://2e.aonprd.com/Spells.aspx?Tradition=2",
        "https://2e.aonprd.com/Spells.aspx?Tradition=3",
        "https://2e.aonprd.com/Spells.aspx?Tradition=4",
        "https://2e.aonprd.com/Spells.aspx?Focus=true&Tradition=0"
    )
    loadSpells(pages.flatMap { spellsFromPage(it) }.toSet(), conditions.map { it.name }.toSet())
}