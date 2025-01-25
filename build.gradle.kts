import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinCompose).apply(false)
    alias(libs.plugins.androidApp).apply(false)
    alias(libs.plugins.androidLib).apply(false)
    alias(libs.plugins.kotlinJvm).apply(false)
    alias(libs.plugins.kotlinKapt).apply(false)
    alias(libs.plugins.anvil).apply(false)
    alias(libs.plugins.mavenPublish).apply(false)
    alias(libs.plugins.binaryValidator)
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
    compileSdkVersion(35)

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

apiValidation {
    ignoredProjects.addAll(listOf("sample", "whetstone-compiler"))
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
