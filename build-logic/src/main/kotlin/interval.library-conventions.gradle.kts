import java.io.FileInputStream
import java.util.Properties
import org.jetbrains.dokka.Platform
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget


plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
}


kotlin {
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget = JvmTarget.JVM_1_8
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        browser {
            useEsModules()
        }
        binaries.executable()
    }
    linuxX64()

    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()
    iosArm64()

    mingwX64()


    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}


// Documentation.
val dokkaJvmJavadoc by tasks.creating(DokkaTask::class) {
    dokkaSourceSets {
        register("jvm") {
            platform.set(Platform.jvm)
            sourceRoots.from(kotlin.sourceSets.getByName("jvmMain").kotlin.srcDirs)
        }
    }
}
val javadocJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Create javadoc jar using Dokka"
    archiveClassifier.set("javadoc")
    from(dokkaJvmJavadoc)
}


// Publish configuration.
val publishProperties = Properties()
val publishPropertiesFile = File("publish.properties")
if (publishPropertiesFile.exists()) {
    publishProperties.load(FileInputStream(publishPropertiesFile))
}
publishing {
    repositories {
        maven {
            name = "local"
            url = uri("${layout.buildDirectory}/repo")
        }
    }
    publications.filterIsInstance<MavenPublication>().forEach {
        if (it.name == "jvm") {
            it.artifact(javadocJar)
        }
        it.pom {
            url.set("https://github.com/Whathecode/kotlinx.interval")
            licenses {
                license {
                    name.set("The Apache Licence, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("Whathecode")
                    name.set("Steven Jeuris")
                    email.set("steven.jeuris@gmail.com")
                }
            }
            scm {
                connection.set("scm:git:git://github.com/Whathecode/kotlinx.interval.git")
                developerConnection.set("scm:git:ssh://github.com/Whathecode/carp.core-kotlin.git")
                url.set("https://github.com/Whathecode/kotlinx.interval")
            }
        }
    }
}
signing {
    val signingKeyFile = publishProperties["signing.keyFile"] as? String
    if (signingKeyFile != null) {
        val signingKey = File(signingKeyFile).readText()
        val signingPassword = publishProperties["signing.password"] as? String
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}
