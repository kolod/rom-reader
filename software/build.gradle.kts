import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.22"
    application
    id("org.openjfx.javafxplugin") version "0.0.13" apply false
}

group = "io.github.kolod"
version = "1.0.0"

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
    implementation("com.formdev:flatlaf:3.2.5")
    implementation("com.formdev:flatlaf-extras:3.2.5")
    
    // Logging
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")
    
    // Testing
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("junit:junit:4.13.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjsr305=strict")
    }
}

application {
    mainClass.set("io.github.kolod.RomReaderKt")
}

tasks.test {
    useJUnit()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "io.github.kolod.RomReaderKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}