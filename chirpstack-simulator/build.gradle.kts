plugins {
    application
    alias(libs.plugins.shadow)
    alias(libs.plugins.gradlejavaqa)
    alias(libs.plugins.integrationtest)
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

    integrationImplementation(libs.testcontainers)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass = "roofsense.chirpstacksimulator.CLI"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
