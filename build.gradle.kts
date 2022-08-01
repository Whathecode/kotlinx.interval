val globalGroup by extra { "io.github.whathecode.kotlinx.interval" }
val globalVersion by extra { "1.0.0-alpha.3" }

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
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
group = globalGroup
version = globalVersion
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
