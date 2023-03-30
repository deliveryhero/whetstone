import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        gradlePluginPortal()
        google()
    }

    dependencies {
        classpath(libs.androidGradle)
        classpath(libs.kotlinGradle)
        classpath(libs.anvilGradle)
        classpath(libs.mavenPublishGradle)
    }
}

subprojects {
    afterEvaluate {
        tasks.withType<KotlinCompile> {
            configureTask()
        }
        extensions.findByType<BaseExtension>()?.apply {
            configureExtension()
        }
    }
}

fun KotlinCompile.configureTask() {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()

        val compilerArgs = mutableListOf(
            "-Xassertions=jvm",
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=com.squareup.anvil.annotations.ExperimentalAnvilApi",
        )
        if (project.name != "sample") compilerArgs += "-Xexplicit-api=strict"
        freeCompilerArgs = freeCompilerArgs + compilerArgs
    }
}

fun BaseExtension.configureExtension() {
    compileSdkVersion(33)

    defaultConfig {
        minSdk = 21
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}


plugins {
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.12.1"
}

apiValidation {
    ignoredProjects.addAll(listOf("sample", "whetstone-compiler"))
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}