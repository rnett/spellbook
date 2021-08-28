package com.rnett.spellbook.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.core.HtmlText
import com.rnett.spellbook.spell.Condition
import com.rnett.spellbook.spell.Trait
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.request.get
import org.jsoup.Jsoup
import java.util.Stack

typealias SidebarNavigator = (SidebarData<*>) -> Unit

//TODO make closable by escape

object SidebarNav {
    val Ambient = compositionLocalOf<SidebarNavigator> { {} }

    @Composable
    fun currentSidebar() = Ambient.current

    @Composable
    fun withNew(sidebarState: SidebarState, content: @Composable () -> Unit) {
        CompositionLocalProvider(Ambient provides sidebarState::new) {
            content()
        }
    }

    @Composable
    fun withGoto(sidebarState: SidebarState, content: @Composable () -> Unit) {
        CompositionLocalProvider(Ambient provides sidebarState::goto) {
            content()
        }
    }

}

val client = HttpClient(Apache)

private suspend fun getAonDana(url: String): Pair<String, AnnotatedString> {
    val document = Jsoup.parse(client.get(url))

    val title = document.select("#ctl00_MainContent_DetailedOutput > h1.title").first()

    val body = document.select("#ctl00_MainContent_DetailedOutput").single().html().removePrefix(title.outerHtml())
        .substringBefore("<h2")
        .substringBefore("<hr")

    return title.text() to HtmlText(body)
}

sealed class SidebarData<D> {
    abstract suspend fun load(): D

    @Composable
    abstract fun BoxScope.title(data: D)

    @Composable
    abstract fun BoxScope.display(data: D)
}

class SidebarState() {
    private val backstack = Stack<SidebarData<*>>()

    var current: SidebarData<*>? by mutableStateOf(null)
        private set

    val active get() = current != null
    val hasStack get() = backstack.isNotEmpty()

    fun new(url: SidebarData<*>) {
        backstack.clear()
        current = url
    }

    fun goto(url: SidebarData<*>) {
        current?.let(backstack::push)
        current = url
    }

    fun back() {
        if (backstack.isNotEmpty()) {
            current = backstack.pop()
        } else {
            close()
        }
    }

    fun close() {
        backstack.clear()
        current = null
    }

    @Composable
    fun withNew(content: @Composable() () -> Unit) {
        SidebarNav.withNew(this, content)
    }

    @Composable
    fun withGoto(content: @Composable() () -> Unit) {
        SidebarNav.withGoto(this, content)
    }
}

class AonUrl(url: String) : SidebarData<Pair<String, AnnotatedString>>() {
    val url: String

    init {
        var url = url.trim('/')
        if (url.startsWith("http://")) {
            url = url.replace("http://", "https://")
        }

        if (!url.startsWith("https://2e.aonprd.com/")) {
            if (url.startsWith("https://"))
                error("Url is not to AON: $url")

            url = "https://2e.aonprd.com/$url"
        }
        this.url = url
    }

    constructor(trait: Trait) : this("Traits.aspx?ID=${trait.aonId}")
    constructor(condition: Condition) : this("Conditions.aspx?ID=${condition.aonId}")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AonUrl) return false

        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }

    override suspend fun load(): Pair<String, AnnotatedString> {
        return getAonDana(url)
    }

    @Composable
    override fun BoxScope.title(data: Pair<String, AnnotatedString>) {
        Text(data.first, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterStart))
    }

    @Composable
    override fun BoxScope.display(data: Pair<String, AnnotatedString>) {
        val sidebar = SidebarNav.currentSidebar()
        ClickableText(
            data.second,
            style = TextStyle(color = MainColors.textColor.asCompose())
        ) {
            val link = data.second.getStringAnnotations("URL", it, it).firstOrNull()?.item
            if (link != null) {

                sidebar(AonUrl(link))
            }
        }
    }
}

@Composable
fun <D> SidebarDisplay(dataLoader: SidebarData<D>, sidebarState: SidebarState) {
    Surface(contentColor = MainColors.textColor.asCompose(), color = MainColors.infoBoxColor.asCompose()) {
        Column(Modifier.fillMaxSize().padding(12.dp)) {

            var data by remember { mutableStateOf<D?>(null) }

            LaunchedEffect(dataLoader) {
                try {
                    data = dataLoader.load()
                } catch (e: Exception) {

                }
            }

            if (data == null) {
                CircularProgressIndicator()
            } else {
                sidebarState.withGoto {
                    Surface(
                        Modifier.fillMaxWidth(),
                        color = MainColors.infoHeaderColor.asCompose(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Box(Modifier.padding(vertical = 4.dp, horizontal = 20.dp)) {
                            dataLoader.apply { title(data!!) }

                            Row(Modifier.align(Alignment.CenterEnd)) {
                                if (sidebarState.hasStack) {
                                    IconButton(sidebarState::back) {
                                        IconWithTooltip(Icons.Filled.ArrowBack, "Back")
                                    }
                                }
                                IconButton(sidebarState::close) {
                                    IconWithTooltip(Icons.Filled.Close, "Close")
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Box(Modifier.fillMaxSize()) {
                        dataLoader.apply { display(data!!) }
                    }
                }
            }
        }
    }
}