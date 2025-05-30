plugins {
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

// Publish configuration.
// For signing and publishing to work, a 'publish.properties' file needs to be added to the root containing:
// The OpenPGP credentials to sign all artifacts:
// > signing.keyFile=<ABSOLUTE PATH TO THE ASCII-ARMORED KEY FILE>
// > signing.password=<SECRET>
// A username and password to upload artifacts to the Sonatype repository:
// > repository.username=<SONATYPE USERNAME>
// > repository.password=<SONATYPE PASSWORD>
val publishProperties = java.util.Properties()
val publishPropertiesFile = File("publish.properties")
if (publishPropertiesFile.exists()) {
    publishProperties.load(java.io.FileInputStream(publishPropertiesFile))
}
group = "io.github.whathecode.kotlinx.interval"
version = "2.1.0"
if (properties.containsKey("snapshot"))
{
    val versionSplit = version.toString().split("-")
    val snapshotVersion = "${versionSplit[0]}-SNAPSHOT"
    version = snapshotVersion
    rootProject.subprojects.forEach { it.version = snapshotVersion }
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
