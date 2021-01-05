package com.rnett.spellbook

import com.rnett.krosstalk.ktor.server.defineKtor
import com.rnett.spellbook.db.SpellbookDB
import com.rnett.spellbook.krosstalk.MyKrosstalk
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.html.*

fun HTML.index() {
    body {
        div {
            id = "root"
        }
        script(src = "/static/spellbook.js") {}
    }
}
//
//fun main() {
//    embeddedServer(Netty) {
//        server()
//    }.start(wait = true)
//}

fun Application.server() {
    SpellbookDB.init()
    MyKrosstalk.defineKtor(this)
    routing {
        get("/") {
            call.respondHtml(HttpStatusCode.OK, HTML::index)
        }
        static("/static") {
            resources("/static")
        }
    }
}