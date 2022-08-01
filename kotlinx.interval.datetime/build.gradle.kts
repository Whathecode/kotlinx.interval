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
            name.set("kotlinx.interval.datetime")
            description.set("Kotlin multiplatform bounded open/closed date/time intervals.")
        }
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":kotlinx.interval"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
            }
        }
        commonTest {
            dependencies {
                implementation(project(":kotlinx.interval.test"))
            }
        }
    }
}
