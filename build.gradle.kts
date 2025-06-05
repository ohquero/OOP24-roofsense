plugins {
    application
    alias(libs.plugins.gradlejavaqa)
    alias(libs.plugins.javafxplugin)
}

repositories {
    mavenCentral()
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    implementation(libs.spotbugs.annotations)

    implementation(libs.commons.lang3)

    implementation(libs.slf4j.api)
    runtimeOnly(libs.logback.classic)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.platform.launcher)
    testImplementation(libs.mockito.core)
    mockitoAgent(libs.mockito.core) { isTransitive = false }
    testImplementation(libs.mockito.junit)
}

tasks.test {
    useJUnitPlatform()
    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    jvmArgs = jvmArgs!!.toMutableList().apply {
        add("-javaagent:${mockitoAgent.asPath}")
    }
}
