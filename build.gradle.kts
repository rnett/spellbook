plugins {
    kotlin("multiplatform") version "1.4.32" apply false
    kotlin("plugin.serialization") version "1.4.32" apply false
    id("com.bnorm.react.kotlin-react-function") version "0.2.1" apply false
//    id("atomicfu-gradle-plugin") version "0.14.4"
    id("com.rnett.krosstalk") version "1.0.3-ALPHA" apply false
}


allprojects {
    group = "com.rnett.spellbook"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        jcenter()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://dl.bintray.com/kotlin/ktor")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://dl.bintray.com/kotlin/kotlinx")
        maven("https://dl.bintray.com/kotlin/kotlin-js-wrappers")
        maven("https://dl.bintray.com/subroh0508/maven")
        maven("https://dl.bintray.com/rnett/krosstalk")
//    mavenLocal()
    }
}