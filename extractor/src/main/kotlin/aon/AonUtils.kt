package com.rnett.spellbook.extractor.aon

import com.rnett.spellbook.extractor.Serialization
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*

val aonClient by lazy {
    HttpClient(Apache) {
        install(ContentEncoding) {
            gzip(1f)
        }
        install(Logging) {
            level = LogLevel.NONE
        }
        install(ContentNegotiation) {
            json(Serialization.IgnoreUnknown)
        }
    }
}