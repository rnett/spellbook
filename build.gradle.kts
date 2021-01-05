import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("multiplatform") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.21"
    application
    id("com.bnorm.react.kotlin-react-function") version "0.2.1"
//    id("atomicfu-gradle-plugin") version "0.14.4"
    id("com.rnett.krosstalk") version "1.0.3-ALPHA"
}
group = "com.rnett.spellbook"
version = "1.0-SNAPSHOT"

val kotlin_version = "1.4.21"
val serialization_version = "1.0.1"
val ktor_version = "1.5.0"
val coroutines_version = "1.4.2"
val atomicfu_version = ""
val exposed_version = "0.28.1"
val pgjdbc_ng_version = "0.8.4"

val wrapper_suffix = "-pre.115-kotlin-1.4.10"
val react_version = "16.13.1$wrapper_suffix"
val styled_version = "1.0.0$wrapper_suffix"
val material_ui_version = "0.5.1"

val jsoup_version = "1.13.1"

val krosstalk_version = "1.0.3-ALPHA"

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
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serialization_version")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
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

                implementation("ch.qos.logback:logback-classic:1.2.3")

                implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
                implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
                implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
                implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng:$pgjdbc_ng_version")

                implementation("io.ktor:ktor-client-apache:$ktor_version")
                implementation("org.jsoup:jsoup:$jsoup_version")
                implementation("me.tongfei:progressbar:0.9.0")
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