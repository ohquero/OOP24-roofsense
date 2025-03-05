plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("com.coditory.integration-test:com.coditory.integration-test.gradle.plugin:2.2.2")
    implementation("com.github.johnrengelman:shadow:8.1.1")
    implementation("org.danilopianini:gradle-java-qa:1.102.0")
}
