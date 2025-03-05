plugins {
    java
    id("com.coditory.integration-test")
    id("com.github.johnrengelman.shadow")
    id("org.danilopianini.gradle-java-qa")
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.17.0")

    implementation("org.slf4j:slf4j-api:2.0.16")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.15")

    implementation("io.reactivex.rxjava3:rxjava:3.1.10")

    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    integrationImplementation("org.testcontainers:testcontainers:1.20.4")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
