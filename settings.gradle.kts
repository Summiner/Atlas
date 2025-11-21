rootProject.name = "Atlas"
include("annotations")
include("core")
include("paper")
include("velocity")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
