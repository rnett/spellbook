package com.rnett.spellbook.krosstalk

import com.rnett.krosstalk.*
import com.rnett.krosstalk.ktor.client.KtorClient
import com.rnett.krosstalk.ktor.client.KtorClientScope

actual object MyKrosstalk : Krosstalk(), KrosstalkClient<KtorClientScope<*>> {
    actual override val serialization: KotlinxJsonObjectSerializationHandler = krosstalkSerialization
    override val client = KtorClient("http://localhost:8080")
}