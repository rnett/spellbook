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
    }
}

enableFeaturePreview("GRADLE_METADATA")

rootProject.name = "spellbook"



