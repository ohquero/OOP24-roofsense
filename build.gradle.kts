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

javafx {
    version = "21"
    modules("javafx.controls", "javafx.controls", "javafx.fxml", "javafx.swing", "javafx.graphics")
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    implementation(libs.spotbugs.annotations)

    implementation(libs.commons.lang3)

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

    // Test database (H2 anche per i test)
    testRuntimeOnly(libs.h2.database)

}


//
// Tests configuration
//

val viewDisplayTestsTag = "view-display"
tasks.named<Test>("test") {
    useJUnitPlatform {
        excludeTags(viewDisplayTestsTag)
    }

    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    jvmArgs = jvmArgs!!.toMutableList().apply {
        add("-javaagent:${mockitoAgent.asPath}")
    }
}
tasks.register<Test>("showView") {
    useJUnitPlatform {
        includeTags(viewDisplayTestsTag)
    }
}
