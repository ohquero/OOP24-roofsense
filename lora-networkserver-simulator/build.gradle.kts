plugins {
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.danilopianini.gradle-java-qa") version "1.78.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.apachecommons.lang3)

    implementation(libs.slf4j.api)
    implementation(libs.logback.classic)

    implementation(libs.rxjava3)

    implementation(libs.paho.client.mqttv3)

    implementation(libs.picocli)

    implementation(libs.jackson.databind)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
