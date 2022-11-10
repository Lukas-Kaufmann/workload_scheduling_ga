import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "lka.ci.scheduling"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.kotlin.link")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.lets-plot:lets-plot-image-export:2.5.1")
    implementation("org.jetbrains.lets-plot:lets-plot-common:2.5.0")
    implementation("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:4.1.0")
    implementation("space.kscience:plotlykt-server:0.5.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}