plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    id(libs.plugins.kotlinx.serialization.get().pluginId)
}

kotlin {
    jvm {
        jvmToolchain(21)
    }
    js {
        browser()
    }
    wasmJs {
        browser()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.bundles.kotlinx.serialization)
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.collections.immutable)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        all {
            languageSettings.apply {
                optIn("kotlin.contracts.ExperimentalContracts")
                optIn("kotlin.time.ExperimentalTime")
                optIn("kotlin.RequiresOptIn")
            }
        }
    }
}