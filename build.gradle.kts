import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.2.10"
    id("org.jetbrains.intellij") version "0.2.17"
}

defaultTasks("build")

val ideaVersion: String by extra
val downloadIdeaSources: String by extra

val javaVersion = JavaVersion.VERSION_1_8
val kotlinVersion = "1.2"

intellij {
    pluginName = "Auto Sync"
    version = ideaVersion
    downloadSources = downloadIdeaSources.toBoolean()
    updateSinceUntilBuild = false
    sandboxDirectory = project.rootDir.canonicalPath + "/.sandbox"
}

repositories {
    mavenCentral()
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib:1.2.10")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1." + javaVersion.majorVersion
        apiVersion = kotlinVersion
        languageVersion = kotlinVersion
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
