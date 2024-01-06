package com.rnett.spellbook

import com.rnett.krosstalk.ktor.server.defineKtor
import com.rnett.spellbook.db.DbSpell
import com.rnett.spellbook.db.SpellbookDB
import com.rnett.spellbook.krosstalk.MyKrosstalk
import io.ktor.application.*
import io.ktor.html.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.html.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction

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
            resources()
        }
    }
}

fun main() {
    SpellbookDB.init()
    transaction {
        println(
            Json { }.encodeToString(
                ListSerializer(Spell.serializer()),
                DbSpell.all().limit(3).map { it.toSpell() })
        )
    }
}