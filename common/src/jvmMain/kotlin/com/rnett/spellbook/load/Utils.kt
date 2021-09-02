package com.rnett.spellbook.load

import kotlinx.coroutines.CoroutineDispatcher
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Slf4jSqlDebugLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import java.util.Locale


fun attrRegex(tag: String, attr: String) =
    Regex("(?<=<$tag[^>]*$attr=['\"])(.+?)(?=[\"'])", RegexOption.DOT_MATCHES_ALL)

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

inline fun <R> loggedTransaction(database: Database? = null, crossinline block: Transaction.() -> R) =
    transaction(database) {
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

inline fun Node.firstSibling(filter: (Node) -> Boolean) =
    firstSiblingOrNull(filter) ?: error("No sibling matching predicate found")

inline fun Node.firstElementSiblingOrNull(filter: (Element) -> Boolean) = firstSiblingOrNull {
    if (it is Element)
        filter(it)
    else
        false
}

inline fun Node.firstElementSibling(filter: (Element) -> Boolean) =
    firstElementSiblingOrNull(filter) ?: error("No sibling matching predicate found")

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

fun Node.siblingsToBreakOrBold(): List<Node> =
    siblingsUntilElement { it.normalTagName == "b" || it.normalTagName == "br" || it.normalTagName == "hr" || it.normalTagName.let { it.length == 2 && it[0] == 'h' } }

fun Node.elementSiblingsToBreak() = siblingsToBreak().filterIsInstance<Element>()

fun List<Node>.elements() = filterIsInstance<Element>()

val Element.normalTagName get() = tag().normalName()

private val inlineElements = setOf("a", "b", "i", "u")

fun List<Node>.textWithNewlines(trim: Boolean = false): String = joinToString("") {
    when (it) {
        is Element -> when {
            it.tagName() == "br" -> "\n"
            it.normalTagName in inlineElements -> it.childNodes().textWithNewlines(trim)
            else -> it.childNodes().textWithNewlines(trim) + "\n"
        }
        is TextNode -> it.text().let {
            if (trim)
                it.trim()
            else
                it.trim('\t', '\n')
        }
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

fun String.enumFormat() = trim(' ', ';').lowercase(Locale.getDefault()).capitalize()