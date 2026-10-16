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

fun loadParentProperties() {
    val properties = Properties()
    file("../gradle.properties").inputStream().use { properties.load(it) }

    properties.forEach { (k, v) ->
        val key = k.toString()
        val value = providers.gradleProperty(key).getOrElse(v.toString())
        extra.set(key, value)
    }

    // java-gradle-plugin's auto-generated "pluginMaven"/marker publications need the project's
    // own group/version set directly; loadParentProperties() only exposed these as extra
    // properties above, which vanniktech's own publication reads but the plugin-marker
    // publication does not.
    group = extra["GROUP"].toString()
    version = extra["VERSION_NAME"].toString()
}

kotlin {
    jvmToolchain(17)
    explicitApi()
    // AGP 9.3.0's own jar targets Java 17, so this module (which has it on its
    // compile classpath via compileOnly) must target at least Java 17 too.
    compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
    implementation(libs.anvilGradle)
    implementation(libs.kspGradle)
    compileOnly(libs.androidGradle)
    compileOnly(libs.kotlinGradle)
}

val generateBuildConfig by tasks.registering(GenerateBuildConfigTask::class) {
    val props = mapOf(
        "GROUP" to project.property("GROUP").toString(),
        "VERSION" to project.property("VERSION_NAME").toString(),
        "DAGGER_VERSION" to libs.versions.dagger.get()
    )
    properties.set(props)
    generatedSourceDir.set(layout.buildDirectory.dir("generated/wgp/kotlin/main"))
}

tasks.named<KotlinCompile>("compileKotlin") {
    dependsOn(generateBuildConfig)
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

