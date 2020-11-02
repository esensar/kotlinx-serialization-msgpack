plugins {
    kotlin("multiplatform") version Dependencies.Versions.kotlin apply false
    kotlin("plugin.serialization") version Dependencies.Versions.kotlin apply false
}

allprojects {
    group = Config.group
    version = Config.version

    repositories {
        mavenLocal()
        mavenCentral()
        jcenter()
    }
}