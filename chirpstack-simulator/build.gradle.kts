plugins {
    application
    id("myproject.java-conventions")
}

dependencies {
    implementation(libs.picocli)
}

application {
    mainClass = "roofsense.chirpstacksimulator.CLI"
}

