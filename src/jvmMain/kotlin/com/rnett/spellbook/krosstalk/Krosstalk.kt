package com.rnett.spellbook.krosstalk

import com.rnett.krosstalk.*
import com.rnett.krosstalk.ktor.server.KtorServer
import com.rnett.krosstalk.ktor.server.KtorServerScope

actual object MyKrosstalk : Krosstalk(), KrosstalkServer<KtorServerScope> {
    actual override val serialization: KotlinxJsonObjectSerializationHandler = krosstalkSerialization
    override val server: ServerHandler<KtorServerScope> = KtorServer

}