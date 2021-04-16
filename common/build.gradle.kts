plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

val serialization_version: String by project
val ktor_version: String by project
val coroutines_version: String by project

val exposed_version: String by project
val pgjdbc_ng_version: String by project
val h2_version: String by project

val jsoup_version: String by project

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
                api("org.jetbrains.kotlinx:kotlinx-serialization-core:$serialization_version")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
                api("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.4")
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
                api("ch.qos.logback:logback-classic:1.2.3")

                api("org.jetbrains.exposed:exposed-core:$exposed_version")
                api("org.jetbrains.exposed:exposed-dao:$exposed_version")
                api("org.jetbrains.exposed:exposed-jdbc:$exposed_version")

                implementation("com.impossibl.pgjdbc-ng:pgjdbc-ng:$pgjdbc_ng_version")
                //TODO I need this for loading, but it should be in Desktop
                implementation("com.h2database:h2:$h2_version")

                api("io.ktor:ktor-client-apache:$ktor_version")
                api("org.jsoup:jsoup:$jsoup_version")
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