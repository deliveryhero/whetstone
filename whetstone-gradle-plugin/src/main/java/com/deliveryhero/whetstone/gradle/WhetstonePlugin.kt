package com.deliveryhero.whetstone.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidBasePlugin
import com.google.auto.service.AutoService
import com.squareup.anvil.plugin.AnvilExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

@AutoService(Plugin::class)
public class WhetstonePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<WhetstoneExtension>(WHETSTONE_EXTENSION)
        target.plugins.apply(ANVIL_PLUGIN_ID)
        target.plugins.withType<AndroidBasePlugin> {
            target.configureAnvil(extension)
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

    private fun Project.configureAnvil(whetstone: WhetstoneExtension) {
        extensions.configure<AnvilExtension> {
            val android = extensions.getByType<BaseExtension>()
            // We apply default setting for anvil here based on whether/not the project
            // is an Android application module
            val isApp = android is AppExtension
            generateDaggerFactories.set(whetstone.generateDaggerFactories.orElse(!isApp))
            syncGeneratedSources.set(whetstone.syncGeneratedSources.orElse(isApp))
        }
    }

    private fun Project.addDependencies(extension: WhetstoneExtension) {
        val useLocal = findProperty("whetstone.internal.project-dependency").toString().toBoolean()

        fun dependency(moduleId: String): Any = when {
            useLocal -> project(":$moduleId")
            else -> "${BuildConfig.GROUP}:$moduleId:${BuildConfig.VERSION}"
        }

        fun DependencyHandlerScope.anvil(moduleId: String) = add("anvil", dependency(moduleId))
        fun DependencyHandlerScope.implementation(moduleId: String) = add("implementation", dependency(moduleId))

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
    }
}
