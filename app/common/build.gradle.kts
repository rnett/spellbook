import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    id(libs.plugins.kotlinx.serialization.get().pluginId)
    alias(libs.plugins.compose.runtime)
    alias(libs.plugins.compose.compiler)
    idea
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

kotlin {
    jvmToolchain(21)
    jvm {
    }
    js {
        browser()
    }
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.ui)
//                api(compose.material)
                api(compose.material3)
                @OptIn(ExperimentalComposeLibrary::class)
                api(compose.components.resources)

                implementation(compose.materialIconsExtended)

                implementation(libs.compose.viewmodel)
                implementation(libs.compose.navigation)

//                @OptIn(ExperimentalComposeLibrary::class)
//                api(compose.desktop.components.splitPane)

                api(libs.kotlinx.datetime)
                api(libs.bundles.kotlinx.serialization)
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.collections.immutable)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.swing)
            }
        }
    }

}

//tasks.processResources {
//    val resources =
//        project(":common").extensions.getByType<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>().sourceSets["jvmMain"].resources
//    from(resources.srcDirs.first()) {
//        includeEmptyDirs = false
//        exclude("application.conf")
//    }
//}
//
//tasks.test {
//    useJUnit()
//}
//
//tasks.withType<KotlinCompile>() {
//    kotlinOptions.jvmTarget = "11"
//}
//
//compose.desktop {
//    application {
//        mainClass = "com.rnett.spellbook.MainKt"
////        nativeDistributions {
////            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
////            packageName = "Spellbook"
////        }
//    }
//}