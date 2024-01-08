import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.compose)
}

compose {
    kotlinCompilerPlugin.set(dependencies.compiler.forKotlin("1.9.21"))
    kotlinCompilerPluginArgs.add("suppressKotlinVersionCompatibilityCheck=1.9.22")
}

val copyJsResourcesWorkaround by tasks.creating(Copy::class) {
    from(project(":app").file("src/commonMain/resources"))
    into("build/processedResources/js/main")
}

val copyWasmResourcesWorkaround by tasks.creating(Copy::class) {
    from(project(":app").file("src/commonMain/resources"))
    into("build/processedResources/wasmJs/main")
}

afterEvaluate {
    tasks.named("jsProcessResources").configure {
        finalizedBy(copyJsResourcesWorkaround)
    }
    tasks.named("jsProductionExecutableCompileSync") {
        dependsOn(copyJsResourcesWorkaround)
    }
    tasks.named("jsDevelopmentExecutableCompileSync") {
        dependsOn(copyJsResourcesWorkaround)
    }
    tasks.named("jsBrowserProductionExecutableDistributeResources") {
        dependsOn(copyJsResourcesWorkaround)
    }
    tasks.named("jsBrowserDevelopmentExecutableDistributeResources") {
        dependsOn(copyJsResourcesWorkaround)
    }
    tasks.named("jsJar") {
        dependsOn(copyJsResourcesWorkaround)
    }

    tasks.named("wasmJsProcessResources").configure {
        finalizedBy(copyWasmResourcesWorkaround)
    }
    tasks.named("wasmJsProductionExecutableCompileSync") {
        dependsOn(copyWasmResourcesWorkaround)
    }
    tasks.named("wasmJsDevelopmentExecutableCompileSync") {
        dependsOn(copyWasmResourcesWorkaround)
    }
}

kotlin {
    js {
        moduleName = "spellbook"
        browser {
            commonWebpackConfig {
                outputFileName = "spellbook.js"
            }
        }
        binaries.executable()
    }

    wasmJs {
        moduleName = "spellbook"
        browser {
            commonWebpackConfig {
                this.outputFileName = "spellbook.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    // Uncomment and configure this if you want to open a browser different from the system default
                    // open = mapOf(
                    //     "app" to mapOf(
                    //         "name" to "google chrome"
                    //     )
                    // )

                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.rootDir.path)
                        add(project.rootDir.path + "/shared/")
                        add(project.rootDir.path + "/app/")
                        add(project.rootDir.path + "/app/web/")
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val jsWasmMain by creating {
            dependencies {
                api(project(":app:common"))
            }
        }
        val jsMain by getting {
            dependsOn(jsWasmMain)
        }
        val wasmJsMain by getting {
            dependsOn(jsWasmMain)
        }
    }
}

compose.experimental {
    web.application {}
}