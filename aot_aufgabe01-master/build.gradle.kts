plugins {
    kotlin("jvm") version "1.9.24"
    id("org.jetbrains.compose") version "1.6.11"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
}

kotlin {
    jvmToolchain(21)
}

compose.desktop {
    application {
        mainClass = "org.example.aot.MainKt"
    }
}

tasks.test {
    useJUnitPlatform()
}
