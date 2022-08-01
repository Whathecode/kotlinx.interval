val globalGroup: String by rootProject.extra
val globalVersion: String by rootProject.extra
group = globalGroup
version = globalVersion

plugins {
    id( "interval.library-conventions" )
}

publishing {
    publications.filterIsInstance<MavenPublication>().forEach {
        it.pom {
            name.set("kotlinx.interval")
            description.set("Kotlin multiplatform bounded open/closed generic intervals.")
        }
    }
}

kotlin {
    sourceSets {
        commonTest {
            dependencies {
                implementation(project(":kotlinx.interval.test"))
            }
        }
    }
}
