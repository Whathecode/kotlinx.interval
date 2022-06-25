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
