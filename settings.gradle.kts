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
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "spellbook"

include(
    "common",
    "desktop",
    "extractor",
    "shared",
    "app:common",
    "app:desktop",
    "app:web"
)



