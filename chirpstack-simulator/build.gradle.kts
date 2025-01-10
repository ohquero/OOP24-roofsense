plugins {
    application
    alias(libs.plugins.shadow)
    alias(libs.plugins.gradlejavaqa)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.apachecommons.lang3)

    implementation(libs.bundles.logging)

    implementation(libs.rxjava3)

    implementation(libs.paho.client.mqttv3)

    implementation(libs.picocli)

    implementation(libs.jackson.databind)

    testImplementation(libs.bundles.junit)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "roofsense.lora.networkserver.simulator.Simulator"
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
