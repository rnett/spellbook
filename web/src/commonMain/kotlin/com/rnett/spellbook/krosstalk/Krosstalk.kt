package com.rnett.spellbook.krosstalk

import com.rnett.krosstalk.KotlinxJsonObjectSerializationHandler
import com.rnett.krosstalk.Krosstalk
import com.rnett.spellbook.Rarity
import com.rnett.spellbook.School
import com.rnett.spellbook.Trait
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import kotlinx.serialization.modules.polymorphic

expect object MyKrosstalk : Krosstalk {
    override val serialization: KotlinxJsonObjectSerializationHandler
}

//TODO remove once https://github.com/Kotlin/kotlinx.serialization/issues/1116 is fixed
val serializerModule = SerializersModule {
    polymorphic(Trait::class) {
        subclass(Trait.Attack::class, Trait.Attack.serializer())
        subclass(Trait.Other::class, Trait.Other.serializer())
        subclass(School::class, School.serializer())
        subclass(Rarity::class, Rarity.serializer())
    }
}

val jsonSerializer = Json {
    serializersModule += serializerModule
}

val krosstalkSerialization = KotlinxJsonObjectSerializationHandler(jsonSerializer)