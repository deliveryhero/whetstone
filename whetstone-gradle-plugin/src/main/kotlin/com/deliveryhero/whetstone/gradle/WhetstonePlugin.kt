package com.deliveryhero.whetstone.gradle

import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.api.variant.impl.capitalizeFirstChar
import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidBasePlugin
import com.squareup.anvil.plugin.AnvilExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import java.io.File
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.hasPlugin
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

public class WhetstonePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<WhetstoneExtension>(WHETSTONE_EXTENSION)
        target.plugins.apply(ANVIL_PLUGIN_ID)
        target.plugins.withType<AndroidBasePlugin> {
            target.configureAnvil(extension)
            // Only setup proguard export for library modules
            // App modules don't export consumer proguard files
            if (!target.isAppModule) {
                target.addLocateWhetstoneProguardTask()
            }
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
    }

    /**
     * Configures proguard rule export for library modules by copying Anvil-generated
     * proguard files to the Kotlin classes output directory where AGP auto-discovers them.
     *
     * Background:
     * - Whetstone code generator (via Anvil) produces .pro files in build/anvil/{variant}/generated/
     * - These files contain -keep rules for classes using @ContributesViewModel, @ContributesFragment, etc.
     * - AGP automatically packages proguard files from META-INF/proguard/ in kotlin-classes into AARs
     *
     * This implementation:
     * - Hooks into KotlinCompile's doLast to copy .pro files after compilation completes
     * - Copies from: build/anvil/{variant}/generated/.pro
     * - Copies to: build/tmp/kotlin-classes/{variant}/META-INF/proguard/.pro
     * - AGP then auto-discovers and packages these files into the AAR's proguard.txt
     *
     * This mimics how KAPT works and ensures proper ProGuard rule propagation to consuming apps.
     */
    private fun Project.addLocateWhetstoneProguardTask() {
        val androidComponents = extensions.findByType(LibraryAndroidComponentsExtension::class.java)
        if (androidComponents == null) {
            logger.warn("Whetstone: LibraryAndroidComponentsExtension not found for $name - skipping proguard configuration")
            return
        }

        androidComponents.onVariants { variant ->
            val variantName = variant.name

            val kotlinCompilationProvider: Provider<KotlinCompilation<*>> = provider {
                extensions
                    .getByType<KotlinAndroidExtension>()
                    .target
                    .compilations
                    .getByName(variantName)
            }

            val compileTaskNameProvider: Provider<String> = kotlinCompilationProvider
                .map { it.compileKotlinTaskName }

            // Copy proguard files as part of KotlinCompile task
            // This ensures they're in kotlin-classes before any AGP task reads from it
            tasks.withType<KotlinCompile> {
                doLast {
                    // Check task name at execution time to avoid configuration-time errors
                    val expectedTaskName = compileTaskNameProvider.get()

                    if (name == expectedTaskName) {
                        val anvilProguardDir = layout.buildDirectory.dir("$ANVIL_GENERATED_SUBPATH/$variantName/generated").get().asFile
                        val kotlinClassesDir = destinationDirectory.get().asFile
                        val targetDir = File(kotlinClassesDir, "META-INF/proguard")

                        // Find all .pro files in Anvil's output
                        val proguardFiles = anvilProguardDir.walk()
                            .filter { it.isFile && it.extension == "pro" }
                            .toList()

                        if (proguardFiles.isNotEmpty()) {
                            targetDir.mkdirs()
                            proguardFiles.forEach { sourceFile ->
                                val targetFile = File(targetDir, sourceFile.name)
                                sourceFile.copyTo(targetFile, overwrite = true)
                            }
                            logger.info("Whetstone: Copied ${proguardFiles.size} proguard file(s) for variant $variantName")
                        }
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
        const val META_INF_PROGUARD_PATH = "META-INF/proguard"
        const val ANVIL_GENERATED_SUBPATH = "anvil"
    }
}
