package com.deliveryhero.whetstone.gradle

import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidBasePlugin
import com.android.build.gradle.internal.tasks.ExportConsumerProguardFilesTask
import com.squareup.anvil.plugin.AnvilExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

internal abstract class ProguardLocatorTask : org.gradle.api.DefaultTask() {

    @get:OutputDirectory
    internal abstract val locatedFiles: DirectoryProperty
}

public class WhetstonePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<WhetstoneExtension>(WHETSTONE_EXTENSION)
        target.plugins.apply(ANVIL_PLUGIN_ID)
        target.plugins.withType<AndroidBasePlugin> {
            target.configureAnvil(extension)
        }
        if (target.isAppModule) {
            target.pluginManager.apply(KAPT_PLUGIN_ID)
            target.dependencies.add(
                "kapt",
                "com.google.dagger:dagger-compiler:${BuildConfig.DAGGER_VERSION}"
            )
        }
        target.afterEvaluate {
            if (!target.plugins.hasPlugin(AndroidBasePlugin::class)) {
                throw GradleException(
                    """
                    Whetstone plugin was applied to project '${target.path}', but could not find
                    a corresponding Android plugin.
                    Whetstone can only be applied to Android projects!
                    """.trimIndent().replace('\n', ' ')
                )
            }
            target.addDependencies(extension)
        }

        target.extensions.findByType<LibraryExtension>()?.apply {
            buildTypes.configureEach {
                val variantName = name.capitalized()
                val sourceTaskName = "compile${variantName}Kotlin"
                val generatedPath = "anvil/${name}/generated/META-INF/proguard"

                val locateWhetstoneProguardTask = target
                    .tasks
                    .register<ProguardLocatorTask>("locateWhetstone${variantName}Proguard") {
                        // This task depends on the task that actually creates the file
                        dependsOn(sourceTaskName)

                        locatedFiles.set(target.layout.buildDirectory.dir(generatedPath))
                    }

                val generatedProguardFiles = locateWhetstoneProguardTask
                    .flatMap { it.locatedFiles }
                    .map { dir ->
                        dir.asFileTree.filter { file -> file.name.endsWith(".pro") }
                    }

                target.tasks.withType<ExportConsumerProguardFilesTask>().configureEach {
                    if (name.contains( variantName, true)) {
                        consumerProguardFiles.from(generatedProguardFiles)
                    }
                }
            }
        }
    }

    private fun Project.configureAnvil(whetstone: WhetstoneExtension) {
        extensions.configure<AnvilExtension> {
            // We apply default setting for anvil here based on whether/not the project
            // is an Android application module
            generateDaggerFactories.set(whetstone.generateDaggerFactories.orElse(!isAppModule))
            syncGeneratedSources.set(whetstone.syncGeneratedSources.orElse(isAppModule))
        }
    }

    private val Project.isAppModule: Boolean
        get() = extensions.getByType<BaseExtension>() is AppExtension

    private fun Project.addDependencies(extension: WhetstoneExtension) {
        val useLocal = findProperty("whetstone.internal.project-dependency").toString().toBoolean()

        fun dependency(moduleId: String): Any = when {
            useLocal -> project(":$moduleId")
            else -> "${BuildConfig.GROUP}:$moduleId:${BuildConfig.VERSION}"
        }

        fun DependencyHandlerScope.anvil(moduleId: String) = add("anvil", dependency(moduleId))
        fun DependencyHandlerScope.implementation(moduleId: String) =
            add("implementation", dependency(moduleId))

        dependencies {
            anvil("whetstone-compiler")
            implementation("whetstone")

            if (extension.addOns.compose.get()) implementation("whetstone-compose")
            if (extension.addOns.workManager.get()) implementation("whetstone-worker")
        }
    }

    private companion object {
        const val ANVIL_PLUGIN_ID = "com.squareup.anvil"
        const val WHETSTONE_EXTENSION = "whetstone"
        const val KAPT_PLUGIN_ID = "kotlin-kapt"
    }
}
