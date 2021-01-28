package com.rnett.spellbook.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.rnett.spellbook.load.normalTagName
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

typealias HTMLRenderer = HtmlContext.(Element) -> Unit

fun Element.style(key: String): String? {
    val styleAttr = attr("style") ?: return null
    val styles = styleAttr.split(";").filterNot { it.isBlank() }.map { it.trim() }.associate {
        val (k, v) = it.split(":").filterNot { it.isBlank() }.map { it.trim() }
        k to v
    }
    return styles[key]
}

class HtmlContext(val builder: AnnotatedString.Builder, private val cont: () -> Unit) {
    fun content() = cont()

    fun withStyle(style: SpanStyle, block: () -> Unit) {
        builder.withStyle(style) {
            block()
        }
    }

    fun withStyle(style: ParagraphStyle, block: () -> Unit) {
        builder.withStyle(style) {
            block()
        }
    }

    fun withAnnotation(tag: String, value: String, block: () -> Unit) {
        val startLength = builder.length
        block()
        val endLength = builder.length
        builder.addStringAnnotation(tag, value, startLength, endLength)
    }

    operator fun String.unaryPlus() = builder.append(this)

    companion object {
        fun withDefaults(render: HTMLRenderer = { content() }): HTMLRenderer = {
            var pushed = 0
            if (it.normalTagName == "i" || it.normalTagName == "em" || it.style("font-style") == "italic") {
                builder.pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                pushed++
            }

            if (it.normalTagName == "b" || it.normalTagName == "strong" || it.style("font-weight") == "bold") {
                builder.pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                pushed++
            }

            if (it.normalTagName == "u" || it.style("text-decoration") == "underline") {
                builder.pushStyle(SpanStyle(textDecoration = TextDecoration.Underline))
                pushed++
            }

            if (it.normalTagName == "sup") {
                builder.pushStyle(SpanStyle(baselineShift = BaselineShift.Superscript))
                pushed++
            }

            if (it.normalTagName == "sub") {
                builder.pushStyle(SpanStyle(baselineShift = BaselineShift.Subscript))
                pushed++
            }

            if (it.normalTagName == "br") {
                +"\n"
            }

            val rest = {
                if (it.normalTagName == "p") {
                    +"\n"
                    render(it)
                    +"\n"
                } else {
                    render(it)
                }

                repeat(pushed) {
                    builder.pop()
                }
            }

            if (it.normalTagName == "a") {
                withAnnotation("URL", it.attr("href"), rest)
            } else {
                rest()
            }
        }
    }
}

fun HtmlText(text: String, handleElement: HTMLRenderer = HtmlContext.withDefaults()): AnnotatedString {
    val html = Jsoup.parse("<span>$text</span>")
    return buildAnnotatedString {
        html.childNodes().forEach {
            renderNode(it, this, handleElement)
        }
    }
}

private fun renderNode(node: Node, builder: AnnotatedString.Builder, handleElement: HTMLRenderer) {
    if (node is Element) {
        HtmlContext(builder) { node.childNodes().forEach { renderNode(it, builder, handleElement) } }
            .handleElement(node)
    } else if (node is TextNode) {
        builder.append(node.text())
    } else {
        error("Unknown node type ${node::class.qualifiedName} for node $node")
    }
}