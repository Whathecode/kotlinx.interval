pluginManagement {
    includeBuild("build-logic")
}

include("kotlinx.interval")
project(":kotlinx.interval").name = "kotlinx-interval"

include("kotlinx.interval.testcases")
project(":kotlinx.interval.testcases").name = "kotlinx-interval-testcases"

include("kotlinx.interval.datetime")
project(":kotlinx.interval.datetime").name = "kotlinx-interval-datetime"
