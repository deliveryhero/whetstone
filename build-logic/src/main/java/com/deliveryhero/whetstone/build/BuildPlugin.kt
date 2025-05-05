package com.deliveryhero.whetstone.build

import com.android.build.api.dsl.CommonExtension
import com.deliveryhero.whetstone.build.Dependency.libs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.*


private const val DETEKT_PLUGINS = "detektPlugins"


class BuildPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.configureJava()
        target.configureKotlin()
        target.configureAndroid()
        target.configureDetekt()
        target.configureLint()
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

    private fun Project.configureDetekt() {
        apply { plugin("io.gitlab.arturbosch.detekt") }

        dependencies {
            add(DETEKT_PLUGINS, libs.findLibrary("detekt-formatting").get())
        }

        extensions.getByType<io.gitlab.arturbosch.detekt.extensions.DetektExtension>().apply {
            config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
            buildUponDefaultConfig = true
            autoCorrect = false
            baseline = file("$rootDir/config/detekt/baseline.xml")
            ignoreFailures = false
        }

        tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
            jvmTarget = "17"
            ignoreFailures = true

            val reportDir = rootProject.layout.buildDirectory.dir("reports/detekt/${project.name}").get().asFile
            reports {
                html.required.set(true)
                html.outputLocation.set(reportDir.resolve("detekt.html"))
                xml.required.set(true)
                xml.outputLocation.set(reportDir.resolve("detekt.xml"))
                sarif.required.set(true)
                sarif.outputLocation.set(reportDir.resolve("detekt.sarif"))
            }

            doFirst {
                reportDir.mkdirs()
            }
        }
    }


    private fun Project.configureLint() {
        plugins.withId("com.android.base") {
            extensions.configure(CommonExtension::class.java) {
                lint {
                    xmlReport = true
                    htmlReport = true

                    val moduleName = project.name
                    val lintReportDir = rootProject.layout.buildDirectory.dir("reports/lint/$moduleName")

                    xmlOutput = lintReportDir.get().file("lint-report.xml").asFile
                    htmlOutput = lintReportDir.get().file("lint-report.html").asFile
                }
            }
        }
    }

}

