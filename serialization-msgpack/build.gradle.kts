plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlinx.benchmark)
    alias(libs.plugins.dokka)
    alias(libs.plugins.ktlint)
    signing
    `maven-publish`
}

kotlin {
    jvm {
        compilations.create("benchmark") {
            associateWith(this@jvm.compilations.getByName("main"))
        }
    }
    android {
        namespace = "$group.core"
        compileSdk = libs.versions.androidTargetSdk.get().toInt()
        minSdk = libs.versions.androidMinSdk.get().toInt()
        lint.targetSdk = libs.versions.androidTargetSdk.get().toInt()
    }
    js {
        compilations.create("benchmark") {
            associateWith(this@js.compilations.getByName("main"))
        }
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs()
    }

    iosArm64()
    iosX64()
    iosSimulatorArm64()
    tvosArm64()
    watchosArm64()
    macosArm64()
    mingwX64()
    linuxX64()
    // TODO: remove deprecated targets in next release
    macosX64()
    tvosX64()
    watchosX64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
        }

        commonMain {
            dependencies {
                api(libs.kotlinx.serialization.core)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        named("jvmBenchmark") {
            dependencies {
                implementation(libs.kotlinx.benchmark.runtime)
                implementation(libs.msgpak.core)
                implementation(libs.msgpak.dataformat)
            }
        }
        named("jsBenchmark") {
            dependencies {
                implementation(libs.kotlinx.benchmark.runtime)
                implementation(npm("@msgpack/msgpack", ">2.0.0 <3.0.0"))
            }
        }
    }
}

benchmark {
    targets {
        register("jvmBenchmark")
        register("jsBenchmark")
    }
}
