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
    archiveVersion.set("")

    // Ensure proper dependency on jar task
    dependsOn(tasks.jar)

    manifest {
        attributes["Main-Class"] = "io.github.kolod.RomReaderKt"
        attributes["Implementation-Title"] = "RomReader"
        attributes["Author"] = "Oleksandr Kolodkin"
        attributes["Created-By"] = "Gradle ${gradle.gradleVersion}"
        attributes["Multi-Release"] = "true"
        attributes["Build-Date"] = SimpleDateFormat("dd/MM/yyyy").format(Date())
    }
    
    // Merge service files properly
    mergeServiceFiles()
    
    // Append license files instead of failing on duplicates
    append("META-INF/LICENSE")
    append("META-INF/LICENSE.txt")
    append("META-INF/NOTICE")
    append("META-INF/NOTICE.txt")
    
    // Transform Log4j2 plugin cache files - ensure plugins are available at runtime
    transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer())
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

// Fix task dependencies for shadow jar related tasks
tasks.named("startShadowScripts") {
    dependsOn("shadowJar")
}

// Task to run the shadow JAR for testing
tasks.register<JavaExec>("runJar") {
    dependsOn("shadowJar")
    group = "application"
    description = "Run the shadow JAR"
    classpath = files(tasks.shadowJar.get().archiveFile)
    mainClass.set("io.github.kolod.RomReaderKt")
}