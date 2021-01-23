package com.rnett.spellbook.components

import androidx.compose.foundation.ClickableText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
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
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.jsoup.Jsoup
import java.util.*

val client = HttpClient()

private suspend fun getAonDana(url: String): Pair<String, AnnotatedString> {
    val document = Jsoup.parse(client.get(url))

    val title = document.select("#ctl00_MainContent_DetailedOutput > h1.title").first()

    val body = document.select("#ctl00_MainContent_DetailedOutput").single().html().removePrefix(title.outerHtml())
        .substringBefore("<h2")
        .substringBefore("<hr")

    return title.text() to HtmlText(body)
}

class SidebarState(){
    private val backstack = Stack<String>()

    var current: String? by mutableStateOf(null)
        private set

    val active get() = current != null
    val hasStack get() = backstack.isNotEmpty()

    fun new(url: String){
        backstack.clear()
        current = url
    }

    fun newFromSidebar(url: String){
        current?.let(backstack::push)
        current = url
    }

    fun back(){
        if(backstack.isNotEmpty()) {
            current = backstack.pop()
        } else {
            close()
        }
    }

    fun close(){
        backstack.clear()
        current = null
    }
}

@Composable
fun InfoListSidebar(aonUrl: String, showBack: Boolean, onClose: () -> Unit, onBack: ()  -> Unit, setSidebar: (String) -> Unit) {
    Surface(contentColor = MainColors.textColor.asCompose(), color = MainColors.infoBoxColor.asCompose()) {
        Column(Modifier.fillMaxSize().padding(12.dp)) {

            var data by remember { mutableStateOf<Pair<String, AnnotatedString>?>(null) }

            LaunchedEffect(aonUrl) {
                try {
                    data = getAonDana(aonUrl)
                } catch (e: Exception){

                }
            }

            if (data == null) {
                CircularProgressIndicator()
            } else {
                Surface(
                    Modifier.fillMaxWidth(),
                    color = MainColors.infoHeaderColor.asCompose(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box(Modifier.padding(vertical = 4.dp, horizontal = 20.dp)) {
                        Text(data!!.first, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterStart))

                        Row(Modifier.align(Alignment.CenterEnd)) {
                            if(showBack) {
                                IconButton(onBack) {
                                    Icon(Icons.Filled.ArrowBack)
                                }
                            }
                            IconButton(onClose) {
                                Icon(Icons.Filled.Close)
                            }
                        }
                    }
                }
                Spacer(Modifier.preferredHeight(20.dp))
                Row(Modifier.fillMaxSize()) {
                    ClickableText(
                        data!!.second,
                        style = TextStyle(color = MainColors.textColor.asCompose())
                    ) {
                        val link = data!!.second.getStringAnnotations("URL", it, it).firstOrNull()?.item
                        if (link != null) {
                            if (link.startsWith("https://2e.aonprd.com/")) {
                                setSidebar(link)
                            } else if("/" !in link){
                                setSidebar("https://2e.aonprd.com/$link")
                            }
                        }
                    }
                }
            }
        }
    }
}