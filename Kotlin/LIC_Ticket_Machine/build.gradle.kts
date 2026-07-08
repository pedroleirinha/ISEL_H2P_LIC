plugins {
    kotlin("jvm") version "2.3.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(files("libs/ticketMachine.jar"))
    implementation("org.postgresql:postgresql:42.7.3")
}

kotlin {
    jvmToolchain(24)
}

tasks.test {
    useJUnitPlatform()
}