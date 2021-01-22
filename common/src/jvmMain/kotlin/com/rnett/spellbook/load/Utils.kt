package com.rnett.spellbook.load

import com.rnett.spellbook.db.Conditions
import com.rnett.spellbook.db.SpellConditions
import com.rnett.spellbook.db.SpellLists
import com.rnett.spellbook.db.SpellTraits
import com.rnett.spellbook.db.SpellbookDB
import com.rnett.spellbook.db.Spells
import com.rnett.spellbook.db.Traits
import kotlinx.coroutines.CoroutineDispatcher
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode


fun attrRegex(tag: String, attr: String) = Regex("(?<=<$tag[^>]*$attr=['\"])(.+?)(?=[\"'])", RegexOption.DOT_MATCHES_ALL)

private val hrefRegex = attrRegex("a", "href")
private val srcRegex = attrRegex("img", "src")

fun adjustAonElement(element: Element): Element {
    if (element.normalTagName == "a") {
        element.attr("href", "https://2e.aonprd.com/${element.attr("href")}")
        element.attr("target", "_blank")
    }
    return element
}

fun adjustAonHtml(html: String): String {
//    val hrefs = hrefRegex.findAll(html).toList()
//    val srcs = srcRegex.findAll(html).toList()

//    var html = html.replace(hrefRegex){ "https://2e.aonprd.com/${it.value}" }

    return html
}

inline fun <R> loggedTransaction(crossinline block: Transaction.() -> R) = transaction {
    addLogger(Slf4jSqlDebugLogger)
    block()
}

suspend inline fun <R> newSuspendedLoggedTransaction(
    context: CoroutineDispatcher?,
    crossinline block: suspend Transaction.() -> R
) = newSuspendedTransaction(context) {
    addLogger(Slf4jSqlDebugLogger)
    block()
}

fun initializeDatabase() {
    SpellbookDB.init()
    loggedTransaction {
        SchemaUtils.createMissingTablesAndColumns(Traits, SpellLists, SpellTraits, Conditions, SpellConditions, Spells)
    }
}

//TODO check db against https://gitlab.com/jrmiller82/pathfinder-2-sqlite  Maybe just use?

inline fun Node.firstSiblingOrNull(filter: (Node) -> Boolean): Node? {
    var current = this.nextSibling()
    val sibs = mutableListOf<Node>()
    while (!filter(current)) {
        sibs += current
        current = current.nextSibling()
    }
    return current
}

inline fun Node.firstSibling(filter: (Node) -> Boolean) = firstSiblingOrNull(filter) ?: error("No sibling matching predicate found")

inline fun Node.firstElementSiblingOrNull(filter: (Element) -> Boolean) = firstSiblingOrNull {
    if (it is Element)
        filter(it)
    else
        false
}

inline fun Node.firstElementSibling(filter: (Element) -> Boolean) = firstElementSiblingOrNull(filter) ?: error("No sibling matching predicate found")

inline fun Node.siblingsUntil(stop: (Node) -> Boolean): List<Node> {
    var current = this.nextSibling()
    val sibs = mutableListOf<Node>()
    while (!stop(current)) {
        sibs += current
        current = current.nextSibling() ?: return sibs
    }
    return sibs
}

inline fun Node.siblingsUntilElement(stop: (Element) -> Boolean) = siblingsUntil {
    it is Element && stop(it)
}

fun Node.siblingsToBreak(): List<Node> =
    siblingsUntilElement { it.normalTagName == "br" || it.normalTagName == "hr" || it.normalTagName.let { it.length == 2 && it[0] == 'h' } }

fun Node.elementSiblingsToBreak() = siblingsToBreak().filterIsInstance<Element>()

fun List<Node>.elements() = filterIsInstance<Element>()

val Element.normalTagName get() = tag().normalName()

fun List<Node>.textWithNewlines(): String = joinToString("") {
    when (it) {
        is Element -> if (it.tagName() == "br") "\n" else it.childNodes().textWithNewlines() + "\n"
        is TextNode -> it.text().trim()
        else -> it.outerHtml()
    }
}

/**
 * -1 -> Reaction
 * 0 -> Free
 * 1, 2, 3 -> Normal
 */
fun actionNumForText(text: String): Int =
    when (text) {
        "Single Action" -> 1
        "Two Actions" -> 2
        "Three Actions" -> 3
        "Reaction" -> -1
        "Free Action" -> 0
        else -> error("Unknown action text $text")
    }

inline fun <reified T> Any?.safeAs() = this as? T

fun String.enumFormat() = trim(' ', ';').toLowerCase().capitalize()