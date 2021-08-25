plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    id(libs.plugins.kotlinx.serialization.get().pluginId)
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/ktor")
    maven("https://dl.bintray.com/kotlin/kotlinx")
//    mavenLocal()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
                useIR = true
            }
        }
    }
    js(IR) {
        browser {
            binaries.executable()
            webpackTask {
                mode =
                    if (project.hasProperty("prod")) org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.PRODUCTION else org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig.Mode.DEVELOPMENT
            }
        }
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
        val jvmMain by getting {
            dependencies {
                api(libs.logback)

                api(libs.bundles.exposed)

                implementation(libs.pgjdbc)
                //TODO I need this for loading, but it should be in Desktop
                implementation(libs.h2)

                api(libs.ktor.client)
                api(libs.jsoup)
                implementation(libs.progressbar)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
                useExperimentalAnnotation("kotlin.time.ExperimentalTime")
                useExperimentalAnnotation("kotlin.RequiresOptIn")
            }
        }
    }
}