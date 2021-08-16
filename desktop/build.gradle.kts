import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.0.0-alpha3"
}

repositories {
    jcenter()
    mavenCentral()
    google()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    testImplementation(kotlin("test-junit"))
    implementation(compose.desktop.currentOs)
    implementation(compose.preview)
    implementation(compose.materialIconsExtended)

    implementation(project(":common"))
}

tasks.processResources {
    val resources =
        project(":common").extensions.getByType<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>().sourceSets["jvmMain"].resources
    from(resources.srcDirs.first()) {
        includeEmptyDirs = false
        exclude("application.conf")
    }
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

compose.desktop {
    application {
        mainClass = "com.rnett.spellbook.MainKt"
//        nativeDistributions {
//            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
//            packageName = "Spellbook"
//        }
    }
}