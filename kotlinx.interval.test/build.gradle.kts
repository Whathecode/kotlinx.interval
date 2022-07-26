plugins {
    id( "interval.library-conventions" )
}

publishing {
    publications.filterIsInstance<MavenPublication>().forEach {
        it.pom {
            name.set("kotlinx.interval.test")
            description.set("Base test classes for extensions of kotlinx.interval.")
        }
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":kotlinx.interval"))
                implementation(kotlin("test"))
            }
        }
        jvmMain {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }
    }
}
