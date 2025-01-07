plugins {
    // foojay-resolver plugin to automatic download JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "OOP24-roofsense"

include("chirpstack-simulator")
