plugins {
    kotlin("multiplatform") version Dependencies.Versions.kotlin apply false
    kotlin("plugin.serialization") version Dependencies.Versions.kotlin apply false
}

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.jlleitschuh.gradle:ktlint-gradle:${Dependencies.Versions.ktlintGradle}")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:${Dependencies.Versions.dokkaGradle}")
    }
}

allprojects {
    group = Config.group
    version = version

    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
    }
}
