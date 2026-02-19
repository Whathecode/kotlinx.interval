import java.io.FileInputStream
import java.util.Properties
import org.jetbrains.dokka.gradle.engine.parameters.KotlinPlatform
import org.jetbrains.dokka.gradle.internal.InternalDokkaGradlePluginApi
import org.jetbrains.dokka.gradle.tasks.DokkaGenerateTask
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
tasks.withType<DokkaGenerateTask>().configureEach {
    // HACK: Dokka 2.0.0 exposes this debug file by default (https://github.com/Kotlin/dokka/issues/3958)
    @OptIn( InternalDokkaGradlePluginApi::class )
    dokkaConfigurationJsonFile.convention( null as RegularFile? )
}
val javadocJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Create javadoc jar using Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaGeneratePublicationHtml)
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
            url = uri("${layout.projectDirectory}/build/repository")
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
