import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    kotlin("jvm") version "2.2.20"
    application
    id("com.gradleup.shadow") version "8.3.8"
    id("org.openjfx.javafxplugin") version "0.0.13" apply false
}

group = "io.github.kolod"
version = SimpleDateFormat("yy.M.d").format(Date())

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // Serial port communication
    implementation("com.fazecast:jSerialComm:2.9.3")
    
    // FlatLaf Look and Feel
    implementation("com.formdev:flatlaf:3.6.1")
    implementation("com.formdev:flatlaf-extras:3.6.1")
    
    // Logging
    implementation("org.apache.logging.log4j:log4j-core:2.25.2")
    implementation("org.apache.logging.log4j:log4j-api:2.25.2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.25.2")
    
    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("junit:junit:4.13.2")
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
    }
}

application {
    mainClass.set("io.github.kolod.RomReaderKt")
}

tasks.test {
    useJUnit()
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("rom-reader")
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "io.github.kolod.RomReaderKt"
    }
    minimize()
}

// Configure application plugin to use shadow jar
tasks.named("distZip") {
    dependsOn("shadowJar")
}

tasks.named("distTar") {
    dependsOn("shadowJar")  
}

tasks.named("startScripts") {
    dependsOn("shadowJar")
}