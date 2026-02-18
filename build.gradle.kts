plugins {
    application
    alias(libs.plugins.gradlejavaqa)
    alias(libs.plugins.javafxplugin)
    alias(libs.plugins.integrationtest)
}

repositories {
    mavenCentral()
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

javafx {
    version = "21"
    modules("javafx.controls", "javafx.controls", "javafx.fxml", "javafx.swing", "javafx.graphics")
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    implementation(libs.spotbugs.annotations)

    implementation(libs.commons.lang3)

    // Google Guice for dependency injection
    implementation(libs.google.guice)

    // Logging
    implementation(libs.slf4j.api)
    runtimeOnly(libs.logback.classic)

    // Validation
    implementation(libs.jakarta.validation.api)
    implementation(libs.hibernate.validator)
    implementation(libs.jakarta.el)

    // JPA and Database
    implementation(libs.jakarta.persistence.api)
    implementation(libs.hibernate.core)
    runtimeOnly(libs.h2.database)

    // Test dependencies
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.platform.launcher)
    testImplementation(libs.mockito.core)
    mockitoAgent(libs.mockito.core) { isTransitive = false }
    testImplementation(libs.mockito.junit)

    // JavaFX test dependencies
    testImplementation(libs.testfx)
    testImplementation(libs.testfx.junit5)
    testImplementation(libs.hamcrest)
    testImplementation(libs.testfx.openjfx.monocle)
}


//
// Tests configuration
//

val showNodeTestsTag = "show-node"

tasks.named<Test>("test") {
    useJUnitPlatform {
        excludeTags(showNodeTestsTag)
    }

    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    jvmArgs = jvmArgs!!.toMutableList().apply {
        add("-javaagent:${mockitoAgent.asPath}")
        add("-Djava.awt.headless=true")
        add("-Dtestfx.robot=glass")
        add("-Dtestfx.headless=true")
        add("-Dprism.order=sw")
    }
}

tasks.named<Test>("integration") {
    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    jvmArgs = jvmArgs!!.toMutableList().apply {
        add("-Djava.awt.headless=true")
        add("-Dtestfx.robot=glass")
        add("-Dtestfx.headless=true")
        add("-Dprism.order=sw")
    }
}

tasks.register<Test>("runShowNodeTest") {
    group = "verification"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath

    useJUnitPlatform {
        includeTags(showNodeTestsTag)
    }
}

//
// JaCoCo (code coverage) settings
//

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.7".toBigDecimal()
            }
        }
    }
}