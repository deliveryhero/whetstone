package com.deliveryhero.whetstone.gradle

import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.File

public class WhetstonePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<WhetstoneExtension>(WHETSTONE_EXTENSION)
        target.plugins.apply(METRO_PLUGIN_ID)
        target.plugins.withType<AndroidBasePlugin> {
            target.configureMetro()
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
    }

    private fun Project.configureMetro() {
        extensions.configure<dev.zacsweers.metro.gradle.MetroPluginExtension> {
            enableKClassToClassMapKeyInterop.set(true)
            interop {
                includeDagger()
                includeAnvilForDagger()
            }
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

        fun DependencyHandlerScope.kotlinCompilerPluginClasspath(moduleId: String) =
            add("kotlinCompilerPluginClasspath", dependency(moduleId))

        fun DependencyHandlerScope.implementation(moduleId: String) =
            add("implementation", dependency(moduleId))

        dependencies {
            kotlinCompilerPluginClasspath("whetstone-compiler")
            implementation("whetstone")

            if (extension.addOns.compose.get()) implementation("whetstone-compose")
            if (extension.addOns.workManager.get()) implementation("whetstone-worker")
        }
    }

    private companion object {
        const val METRO_PLUGIN_ID = "dev.zacsweers.metro"
        const val WHETSTONE_EXTENSION = "whetstone"
    }
}
