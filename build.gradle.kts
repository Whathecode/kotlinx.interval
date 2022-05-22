import java.io.FileInputStream
import java.util.Properties
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.Platform
import org.jetbrains.dokka.gradle.DokkaTask


plugins {
    kotlin("multiplatform") version "1.6.20"
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.6.21"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

group = "io.github.whathecode.kotlinx.interval"
version = "1.0.0-alpha.1"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(BOTH) {
        browser { }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}


// Publish configuration.
// For signing and publishing to work, a 'publish.properties' file needs to be added to the root containing:
// The OpenPGP credentials to sign all artifacts:
// > signing.keyFile=<ABSOLUTE PATH TO THE ASCII-ARMORED KEY FILE>
// > signing.password=<SECRET>
// A username and password to upload artifacts to the Sonatype repository:
// > repository.username=<SONATYPE USERNAME>
// > repository.password=<SONATYPE PASSWORD>
val publishProperties = Properties()
val publishPropertiesFile = File("publish.properties")
if (publishPropertiesFile.exists()) {
    publishProperties.load(FileInputStream(publishPropertiesFile))
}
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
publishing {
    repositories {
        maven {
            name = "local"
            url = uri("$buildDir/repo")
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
nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(publishProperties["repository.username"] as? String)
            password.set(publishProperties["repository.password"] as? String)
        }
    }
}
val setSnapshotVersion by tasks.creating {
    doFirst {
        val versionSplit = version.toString().split("-")
        val snapshotVersion = "${versionSplit[0]}-SNAPSHOT"
        version = snapshotVersion
    }
}
