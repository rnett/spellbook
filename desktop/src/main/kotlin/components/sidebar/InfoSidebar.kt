package com.rnett.spellbook.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.ArrowBack
import androidx.compose.material3.icons.filled.OpenInNew
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.core.HtmlText
import com.rnett.spellbook.components.sidebar.SidebarSurface
import com.rnett.spellbook.load.mainContentId
import com.rnett.spellbook.spell.AonLinkable
import com.rnett.spellbook.withBackoff
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.request.*
import org.jsoup.Jsoup
import java.util.*

typealias SidebarNavigator = (InfoSidebarData<*>) -> Unit

object SidebarNav {
    val LocalSidebar = compositionLocalOf<SidebarNavigator> { {} }

    @Composable
    fun currentSidebar() = LocalSidebar.current

    @Composable
    fun withNew(sidebarState: InfoSidebarState, content: @Composable () -> Unit) {
        CompositionLocalProvider(LocalSidebar provides sidebarState::new) {
            content()
        }
    }

    @Composable
    fun withGoto(sidebarState: InfoSidebarState, content: @Composable () -> Unit) {
        CompositionLocalProvider(LocalSidebar provides sidebarState::goto) {
            content()
        }
    }

}

val client = HttpClient(Apache)

private suspend fun getAonDana(url: String): Pair<String, AnnotatedString> {
    val document = Jsoup.parse(client.get(url))

    val title = document.select("#$mainContentId > h1.title").first()

    val body = document.select("#$mainContentId").single().html().removePrefix(title.outerHtml())
        .substringBefore("<h2")
        .substringBefore("<hr")

    return title.text() to HtmlText(body)
}

abstract class InfoSidebarData<D> {
    abstract suspend fun load(): D

    @Composable
    abstract fun BoxScope.title(data: D)

    @Composable
    open fun RowScope.controlButtons(data: D) {

    }

    @Composable
    abstract fun BoxScope.display(data: D)

    @Composable
    abstract fun ColumnScope.errorMessage(exception: Throwable)
}

class InfoSidebarState(val openSidebar: () -> Unit, val closeSidebar: () -> Unit) {
    private val backstack = Stack<InfoSidebarData<*>>()

    var current: InfoSidebarData<*>? by mutableStateOf(null)
        private set

    val hasCurrent get() = current != null
    val hasStack get() = backstack.isNotEmpty()

    fun new(url: InfoSidebarData<*>) {
        backstack.clear()
        current = url
        openSidebar()
    }

    fun goto(url: InfoSidebarData<*>) {
        current?.let(backstack::push)
        current = url
        openSidebar()
    }

    fun back() {
        if (backstack.isNotEmpty()) {
            current = backstack.pop()
        } else {
            closeSidebar()
        }
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

class AonUrl(url: String) : InfoSidebarData<Pair<String, AnnotatedString>>() {
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

    constructor(linkable: AonLinkable) : this(linkable.aonUrl)

    override suspend fun load(): Pair<String, AnnotatedString> {
        return getAonDana(url)
    }

    @Composable
    override fun BoxScope.title(data: Pair<String, AnnotatedString>) {
        Text(data.first, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterStart))
    }

    @Composable
    override fun RowScope.controlButtons(data: Pair<String, AnnotatedString>) {
        IconButtonHand({
            openInBrowser(url)
        }
        ) {
            IconWithTooltip(Icons.Filled.OpenInNew, "Open in browser")
        }
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

    @Composable
    override fun ColumnScope.errorMessage(exception: Throwable) {
        Text("Error loading AoN, are you connected to the internet?")
        Row {
            Text("URL:")
            Spacer(Modifier.width(5.dp))
            Text(url, Modifier.clickable {
                openInBrowser(url)
            }, textDecoration = TextDecoration.Underline, fontStyle = FontStyle.Italic)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AonUrl) return false

        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }

    override fun toString(): String {
        return "AonUrl($url)"
    }
}

sealed class InfoData {
    data class Data(val data: Any?) : InfoData()
    object Waiting : InfoData()
    data class Error(val exception: Throwable) : InfoData()
}

@Composable
fun SidebarInfoDisplay(sidebarState: InfoSidebarState) {
    val dataLoader = (sidebarState.current ?: return) as InfoSidebarData<Any?>
    var data by remember { mutableStateOf<InfoData>(InfoData.Waiting) }

    LaunchedEffect(dataLoader) {
        data = try {
            InfoData.Data(withBackoff { dataLoader.load() })
        } catch (t: Throwable) {
            InfoData.Error(t)
        }
    }


    val modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)

    SidebarSurface(
        if (data is InfoData.Data) {
            {
                val value = (data as InfoData.Data).data
                dataLoader.apply { title(value) }

                Row(Modifier.align(Alignment.CenterEnd)) {
                    if (sidebarState.hasStack) {
                        IconButton(sidebarState::back) {
                            IconWithTooltip(Icons.Filled.ArrowBack, "Back")
                        }
                    }

                    dataLoader.apply { controlButtons(value) }
                }
            }
        } else null,
        sidebarState.closeSidebar,
        sidebarState.hasCurrent
    ) {
        when (val d = data) {
            is InfoData.Data -> {
                val value = d.data
                sidebarState.withGoto {
                    Box(modifier) {
                        dataLoader.apply { display(value) }
                    }
                }
            }
            is InfoData.Waiting -> {
                Box(modifier, Alignment.Center) {
                    CircularProgressIndicator(Modifier.fillMaxWidth(0.3f))
                }
            }
            is InfoData.Error -> {
                Column(
                    modifier.padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    dataLoader.apply { errorMessage(d.exception) }
                }
            }
        }
    }
}