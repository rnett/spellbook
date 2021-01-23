import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    application
    //TODO this is not up to date for 1.4.20
//    id("com.bnorm.react.kotlin-react-function")
    //TODO this is not updated either
//    id("com.rnett.krosstalk")
}

val ktor_version: String by project

val wrapper_suffix: String by project
val react_version_prefix: String by project
val styled_version_prefix: String by project
val react_version = react_version_prefix + wrapper_suffix
val styled_version = styled_version_prefix + wrapper_suffix
val material_ui_version: String by project

val krosstalk_version: String by project

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/ktor")
    maven("https://dl.bintray.com/kotlin/kotlinx")
    maven("https://dl.bintray.com/kotlin/kotlin-js-wrappers")
    maven("https://dl.bintray.com/subroh0508/maven")
    maven("https://dl.bintray.com/rnett/krosstalk")
//    mavenLocal()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "13"
                useIR = true
            }
        }
        withJava()
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
                implementation("com.rnett.krosstalk:krosstalk:$krosstalk_version")

                implementation(project(":common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
//                implementation("com.rnett.krosstalk:krosstalk:$krosstalk_version")
                implementation("com.rnett.krosstalk:krosstalk-ktor-server:$krosstalk_version")
                implementation("io.ktor:ktor-server-netty:$ktor_version")
                implementation("io.ktor:ktor-html-builder:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
            }

            tasks.processResources{
                from(project(":common").kotlin.sourceSets["jvmMain"].resources)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting {
            dependencies {
//                implementation("com.rnett.krosstalk:krosstalk:$krosstalk_version")
                implementation("com.rnett.krosstalk:krosstalk-ktor-client:$krosstalk_version")
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.2")
                implementation("org.jetbrains:kotlin-react:$react_version")
                implementation("org.jetbrains:kotlin-react-dom:$react_version")
                implementation("org.jetbrains:kotlin-styled:$styled_version")
                implementation("com.bnorm.react:kotlin-react-function:0.2.1")

                implementation("net.subroh0508.kotlinmaterialui:core:$material_ui_version")
                implementation("net.subroh0508.kotlinmaterialui:lab:$material_ui_version")
//
//                implementation(npm("react", "16.13.1"))
//                implementation(npm("react-dom", "16.13.1"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
//        all{
//            languageSettings.apply{
//                enableLanguageFeature("InlineClasses")
//                enableLanguageFeature("NewInference")
//                useExperimentalAnnotation("kotlin.contracts.ExperimentalContracts")
//                useExperimentalAnnotation("kotlin.RequiresOptIn")
//                useExperimentalAnnotation("kotlin.time.ExperimentalTime")
//            }
//        }
    }
}
application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

tasks.create<JavaExec>("runImport") {
    dependsOn("jvmMainClasses")
    group = "application"
    val compilation = kotlin.targets["jvm"].compilations["main"] as org.jetbrains.kotlin.gradle.plugin.KotlinCompilationToRunnableFiles<*>

    classpath = compilation.runtimeDependencyFiles + compilation.output.allOutputs

    main = "com.rnett.spellbook.load.LoadKt"
}

tasks.getByName<KotlinWebpack>("jsBrowserProductionWebpack") {
    outputFileName = "spellbook.js"
}
tasks.getByName<Jar>("jvmJar") {
    dependsOn(tasks.getByName("jsBrowserProductionWebpack"))
    val jsBrowserProductionWebpack = tasks.getByName<KotlinWebpack>("jsBrowserProductionWebpack")
    from(File(jsBrowserProductionWebpack.destinationDirectory, jsBrowserProductionWebpack.outputFileName)) {
        into("static")
    }

}
tasks.getByName<JavaExec>("run") {
    dependsOn(tasks.getByName<Jar>("jvmJar"))
    classpath(tasks.getByName<Jar>("jvmJar"))
}