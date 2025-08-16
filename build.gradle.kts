import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
    kotlin("plugin.jpa") version "2.0.0"
    kotlin("plugin.allopen") version "2.0.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    id("com.github.ben-manes.versions") version "0.52.0"
}

group = "org.yosefdreams"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val testcontainersVersion = "1.19.8"
val mockkVersion = "1.13.10"
val assertjVersion = "3.25.3"
val kotlinxSerializationVersion = "1.6.3"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    
    // Database
    runtimeOnly("com.mysql:mysql-connector-j:8.3.0")
    
    // Development Only
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    
    // Test Dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    testImplementation("org.springframework.security:spring-security-test")
    
    // Testcontainers
    testImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:mysql:$testcontainersVersion")
    
    // MockK for Kotlin testing
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.mockk:mockk-agent-jvm:$mockkVersion")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    
    // AssertJ for fluent assertions
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    
    // H2 Database for testing
    testRuntimeOnly("com.h2database:h2:2.2.224")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
    systemProperty("spring.profiles.active", "test")
}

// For JPA entities
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

// Enable Gradle's configuration cache
// Uncomment if you want to use Gradle's configuration cache
// tasks.withType<AbstractCompile> {
//     options.isIncremental = true
// }

// Add tasks for building and running the application
// These tasks are more efficient than the standard bootRun task
tasks.register<JavaExec>("bootRunDev") {
    group = "application"
    description = "Runs the Spring Boot application with the dev profile"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.yosefdreams.diary.YosefApplicationKt")
    args("--spring.profiles.active=dev")
}
