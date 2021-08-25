enableFeaturePreview("VERSION_CATALOGS")

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
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}

rootProject.name = "spellbook"

include("common", "desktop")



