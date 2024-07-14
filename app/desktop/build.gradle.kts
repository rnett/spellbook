plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.compose.runtime)
    alias(libs.plugins.compose.compiler)
}

compose {
    kotlinCompilerPlugin.set(dependencies.compiler.forKotlin("1.9.21"))
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.9.22")
}

kotlin {
    jvmToolchain(21)
    jvm {
        withJava()
    }

    sourceSets {
        jvmMain {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.preview)
                implementation(project(":app:common"))
            }
        }
        jvmTest {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }

}

compose.desktop {
    application {
        mainClass = "com.rnett.spellbook.DesktopMainKt"
//        nativeDistributions {
//            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
//            packageName = "Spellbook"
//        }
    }
}