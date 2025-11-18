rootProject.name = "Atlas"
include("core")
include("annotations")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}