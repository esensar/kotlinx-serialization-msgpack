plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.benchmark")
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvm {
        compilations.create("benchmark") {
            associateWith(this@jvm.compilations.getByName("main"))
        }
    }
    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }
    iosX64()
    iosArm64()
    iosX64()
    iosSimulatorArm64()
    tvosX64()
    tvosArm64()
    watchosX64()
    watchosArm64()
    macosX64()
    macosArm64()
    mingwX64()
    linuxX64()

    fun kotlinx(
        name: String,
        version: String,
    ): String = "org.jetbrains.kotlinx:kotlinx-$name:$version"

    fun kotlinxSerialization(name: String) = kotlinx("serialization-$name", Dependencies.Versions.serialization)

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }

        commonMain {
            dependencies {
                implementation(project(":serialization-msgpack"))
                implementation(kotlinx("datetime", Dependencies.Versions.datetime))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        jvmTest {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jvmBenchmark by getting {
            dependencies {
                implementation(kotlinx("benchmark-runtime", Dependencies.Versions.benchmark))
            }
        }
        jsTest {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

benchmark {
    targets {
        register("jvmBenchmark")
    }
}
