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

val snapshot: String? by project
val sonatypeStaging = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
val sonatypeSnapshots = "https://oss.sonatype.org/content/repositories/snapshots"

val sonatypePassword: String? by project
val sonatypeUsername: String? by project

val sonatypePasswordEnv: String? = System.getenv()["SONATYPE_PASSWORD"]
val sonatypeUsernameEnv: String? = System.getenv()["SONATYPE_USERNAME"]

allprojects {
    group = Config.group
    version = version.toString() + if (snapshot == "true") "-SNAPSHOT" else ""

    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

subprojects {
    afterEvaluate {
        apply(plugin = "maven-publish")
        apply(plugin = "signing")
        apply(plugin = "org.jetbrains.dokka")

        val dokkaHtml = tasks["dokkaHtml"]
        tasks {
            create<Jar>("javadocJar") {
                dependsOn(dokkaHtml)
                archiveClassifier.set("javadoc")
                from(dokkaHtml)
            }
        }

        configure<SigningExtension> {
            isRequired = false
            sign(extensions.getByType<PublishingExtension>().publications)
        }

        configure<PublishingExtension> {
            publications.withType(MavenPublication::class) {
                artifact(tasks["javadocJar"])
                pom {
                    name.set("Kotlinx Serialization MsgPack")
                    description.set("MsgPack format support for kotlinx.serialization")
                    url.set("https://github.com/esensar/kotlinx-serialization-msgpack")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/mit-license.php")
                        }
                    }
                    developers {
                        developer {
                            id.set("esensar")
                            name.set("Ensar Sarajčić")
                            url.set("https://ensarsarajcic.com")
                            email.set("es.ensar@gmail.com")
                        }
                    }
                    scm {
                        url.set("https://github.com/esensar/kotlinx-serialization-msgpack")
                        connection.set("scm:git:https://github.com/esensar/kotlinx-serialization-msgpack.git")
                        developerConnection.set("scm:git:git@github.com:esensar/kotlinx-serialization-msgpack.git")
                    }
                }
            }
            repositories {
                maven {
                    url = uri(sonatypeStaging)
                    credentials {
                        username = sonatypeUsername ?: sonatypeUsernameEnv ?: ""
                        password = sonatypePassword ?: sonatypePasswordEnv ?: ""
                    }
                }

                maven {
                    name = "snapshot"
                    url = uri(sonatypeSnapshots)
                    credentials {
                        username = sonatypeUsername ?: sonatypeUsernameEnv ?: ""
                        password = sonatypePassword ?: sonatypePasswordEnv ?: ""
                    }
                }

                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/esensar/kotlinx-serialization-msgpack")
                    credentials {
                        username = System.getenv("GITHUB_ACTOR")
                        password = System.getenv("GITHUB_TOKEN")
                    }
                }
            }
        }
    }
}
