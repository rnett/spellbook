pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "atomicfu-gradle-plugin")
                useModule("org.jetbrains.kotlinx:atomicfu-gradle-plugin:${requested.version}")
        }
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
        jcenter()
        maven("https://dl.bintray.com/kotlin/kotlinx")
        maven("https://kotlin.bintray.com/kotlinx")
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}

rootProject.name = "spellbook"

include("common", /*"web", */"desktop")



