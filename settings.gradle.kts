rootProject.name = "OOP24-roofsense"

plugins {
    // foojay-resolver plugin to automatic download JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

include("chirpstack-simulator")
