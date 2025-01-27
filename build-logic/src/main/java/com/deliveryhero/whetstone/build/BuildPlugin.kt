package com.deliveryhero.whetstone.build

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.*

class BuildPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configureJava()
        target.configureKotlin()
        target.configureAndroid()
    }

    private fun Project.configureJava() = configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    private fun Project.configureKotlin() = configure<KotlinProjectExtension> {
        val config: KotlinJvmCompilerOptions.() -> Unit = {
            optIn.add("com.squareup.anvil.annotations.ExperimentalAnvilApi")
            freeCompilerArgs.addAll("-Xjvm-default=all", "-Xassertions=jvm")
            jvmTarget.set(JvmTarget.JVM_11)
        }
        when (this) {
            is KotlinJvmProjectExtension -> compilerOptions(config)
            is KotlinAndroidProjectExtension -> compilerOptions(config)
        }
        if (project.name != "sample") explicitApi()
        jvmToolchain(17)
    }

    private fun Project.configureAndroid() = plugins.withId("com.android.base") {
        extensions.configure(CommonExtension::class) {
            compileSdk = 35
            defaultConfig.minSdk = 21
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }
        }
    }
}
