plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.compose)
}

compose {
    kotlinCompilerPlugin.set(dependencies.compiler.forKotlin("1.9.21"))
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.9.22")
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
        commonMain {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.ui)
                api(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                api(compose.material3)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                api(compose.components.resources)

                api(libs.kotlinx.datetime)

                implementation(project(":shared"))
//                implementation(project(":common"))
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