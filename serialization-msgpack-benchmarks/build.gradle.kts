@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.benchmark)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ktlint)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get().toInt()))
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
    jvmToolchain(libs.versions.java.get().toInt())
    jvm()
    js {
        browser()
        nodejs()
    }
    wasmJs {
        browser()
        nodejs()
    }
    applyDefaultHierarchyTemplate()
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":serialization-msgpack"))
                implementation(libs.kotlinx.benchmark.runtime)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.msgpak.core)
                implementation(libs.msgpak.dataformat)
            }
        }
        webMain {
            dependencies {
                implementation(npm("@msgpack/msgpack", ">2.0.0 <3.0.0"))
                implementation(libs.kotlin.wrappers.browser)
            }
        }
    }
}

benchmark {
    targets {
        register("jvm")
        register("js")
        register("wasmJs")
    }
}