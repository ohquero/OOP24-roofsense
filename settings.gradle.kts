rootProject.name = "OOP24-roofsense"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

includeBuild("build-conventions")
include("chirpstack-simulator", "dashboard")
