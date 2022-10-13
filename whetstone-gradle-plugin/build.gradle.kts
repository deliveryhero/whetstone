import java.util.*

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    id("java-library")
    id("java-gradle-plugin")
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.mavenPublish).apply(false)
}

loadParentProperties()
pluginManager.apply(com.vanniktech.maven.publish.MavenPublishPlugin::class)

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.compileKotlin {
    dependsOn(generateBuildConfig)
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xexplicit-api=strict"
}

gradlePlugin {
    plugins {
        create("whetstone") {
            id = "com.deliveryhero.whetstone"
            implementationClass = "com.deliveryhero.whetstone.gradle.WhetstonePlugin"
        }
    }
}

dependencies {
    implementation(gradleKotlinDsl())
    implementation(libs.anvilGradle)
    compileOnly(libs.androidGradle)
    compileOnly(libs.autoServiceAnnotations)
    kapt(libs.autoServiceCompiler)
}

val generateBuildConfig by tasks.registering(GenerateBuildConfigTask::class) {
    val props = mapOf(
        "GROUP" to project.property("GROUP").toString(),
        "VERSION" to project.property("VERSION_NAME").toString(),
        "DAGGER_VERSION" to libs.versions.dagger.get()
    )
    properties.set(props)
    generatedSourceDir.set(layout.buildDirectory.dir("generated/wgp/kotlin/main"))
    sourceSets.main.get().java.srcDir(generatedSourceDir)
}

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

fun loadParentProperties() {
    val properties = Properties()
    file("../gradle.properties").inputStream().use { properties.load(it) }

    properties.forEach { (k, v) ->
        val key = k.toString()
        val value = providers.gradleProperty(name).getOrElse(v.toString())
        extra.set(key, value)
    }
}
