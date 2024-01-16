plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    id(libs.plugins.kotlinx.serialization.get().pluginId)
}

dependencies {
    testImplementation(kotlin("test-junit"))
    implementation(libs.bundles.kotlinx.serialization)
    implementation(libs.jsoup)
    implementation(libs.bundles.ktor.client)
    implementation(kotlin("reflect"))
}

kotlin {
    jvmToolchain(21)
}