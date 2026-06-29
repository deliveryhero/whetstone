import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    id("java-gradle-plugin")
    `kotlin-dsl`
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.mavenPublish)
}

loadParentProperties()

group = extra["GROUP"] ?: "com.deliveryhero.whetstone"
version = extra["VERSION_NAME"] ?: "1.1.8-SNAPSHOT"

fun loadParentProperties() {
    val properties = Properties()
    file("../gradle.properties").inputStream().use { properties.load(it) }

    properties.forEach { (k, v) ->
        val key = k.toString()
        val value = providers.gradleProperty(key).getOrElse(v.toString())
        extra.set(key, value)
    }
}

kotlin {
    jvmToolchain(21)
    explicitApi()
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

gradlePlugin {
    plugins {
        create("whetstone") {
            id = "com.deliveryhero.whetstone"
            implementationClass = "com.deliveryhero.whetstone.gradle.WhetstonePlugin"
        }
    }
}

tasks.named<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}

dependencies {
    implementation(libs.metroGradle)
    compileOnly(libs.androidGradle)
    compileOnly(libs.kotlinGradle)
}

val generateBuildConfig by tasks.registering(GenerateBuildConfigTask::class) {
    val props = mapOf(
        "GROUP" to project.property("GROUP").toString(),
        "VERSION" to project.property("VERSION_NAME").toString(),
        "METRO_VERSION" to libs.versions.metro.get()
    )
    properties.set(props)
    generatedSourceDir.set(layout.buildDirectory.dir("generated/wgp/kotlin/main"))
}

tasks.named<KotlinCompile>("compileKotlin") {
    dependsOn(generateBuildConfig)
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
    }
}

// Lazily add the generated source directory to the main source set.
sourceSets.main.get().java.srcDir(generateBuildConfig.flatMap { it.generatedSourceDir })

abstract class GenerateBuildConfigTask : DefaultTask() {
    @get:Input
    abstract val properties: MapProperty<String, String>

    @get:OutputDirectory
    abstract val generatedSourceDir: DirectoryProperty

    @TaskAction
    fun taskAction() {
        val buildFile = generatedSourceDir.file("com/deliveryhero/whetstone/gradle/BuildConfig.kt")

        buildFile.get().asFile.run {
            parentFile.mkdirs()
            val content = buildString {
                appendLine("package com.deliveryhero.whetstone.gradle")
                appendLine()
                appendLine("internal object BuildConfig {")
                properties.get().forEach { (k, v) -> appendLine("  const val $k = \"$v\"") }
                appendLine("}")
            }
            writeText(content)
        }
    }
}

afterEvaluate {
    with(tasks) {
        findByName("kaptGenerateStubsKotlin")?.dependsOn(findByName("generateBuildConfig"))
    }
}
