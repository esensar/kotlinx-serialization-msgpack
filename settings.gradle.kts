rootProject.name = "kotlinx-serialization-msgpack"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        gradlePluginPortal()
        maven("https://central.sonatype.com/repository/maven-snapshots")
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://central.sonatype.com/repository/maven-snapshots")
    }
}

include("serialization-msgpack")
include("serialization-msgpack-timestamp-extension")
include("serialization-msgpack-unsigned-support")
include("serialization-msgpack-benchmarks")