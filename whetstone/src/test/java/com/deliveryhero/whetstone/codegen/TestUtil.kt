@file:OptIn(ExperimentalCompilerApi::class)

package com.deliveryhero.whetstone.codegen

import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.app.ApplicationComponent
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import com.squareup.anvil.compiler.internal.testing.AnvilCompilationMode
import com.squareup.anvil.compiler.internal.testing.ComponentProcessingMode
import com.squareup.anvil.compiler.internal.testing.compileAnvil
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.tschuchort.compiletesting.JvmCompilationResult
import dagger.Binds
import dagger.MembersInjector
import dagger.Module
import dagger.multibindings.IntoMap
import dagger.multibindings.LazyClassKey
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.File
import javax.inject.Singleton
import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.reflect.typeOf
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.text.replace

/**
 * Compiles [sources] against Whetstone's Anvil-KSP based codegen (see
 * [WhetstoneAnvilKspExtension][com.deliveryhero.whetstone.compiler.WhetstoneAnvilKspExtension]).
 *
 * kctfork's bundled compiler-embeddable expects an older metadata version than this project's
 * pinned Kotlin, so `-Xskip-metadata-version-check` is required for KSP-mode test compilations.
 */
internal fun compileWhetstone(
    @Language("kotlin") vararg sources: String,
    generateDaggerFactories: Boolean = false,
    componentProcessingMode: ComponentProcessingMode = ComponentProcessingMode.NONE,
    block: JvmCompilationResult.() -> Unit = {},
): JvmCompilationResult = compileAnvil(
    *sources,
    generateDaggerFactories = generateDaggerFactories,
    componentProcessingMode = componentProcessingMode,
    mode = AnvilCompilationMode.Ksp(),
    onCompilation = {
        kotlinCompilation.kotlincArguments += "-Xskip-metadata-version-check"
    },
    block = block,
)

internal fun JvmCompilationResult.validateInstanceBinding(
    classUnderTest: String,
    baseClass: KClass<*>,
    scope: KClass<*>
) {
    val clas = classLoader.loadClass(classUnderTest).kotlin
    val bindingModule = classLoader.loadClass("${classUnderTest}BindingsModule").kotlin

    assertTrue(bindingModule.hasAnnotation<Module>())
    assertEquals(scope, bindingModule.findAnnotation<ContributesTo>()?.scope)

    val bindsMethod = bindingModule.declaredMemberFunctions.single { it.name == "binds" }
    assertTrue(bindsMethod.hasAnnotation<Binds>())
    assertTrue(bindsMethod.hasAnnotation<IntoMap>())
    assertEquals(clas, bindsMethod.findAnnotation<LazyClassKey>()?.value)
    assertEquals(clas, bindsMethod.findParameterByName("target")?.type?.classifier)
    assertEquals(baseClass, bindsMethod.returnType.classifier)
}

internal fun JvmCompilationResult.validateLazyBindingKey(classUnderTest: String) {
    val clas = classLoader.loadClass(classUnderTest).kotlin
    val className = "${classUnderTest}BindingsModule_Binds_LazyMapKey"
    val generatedClass = classLoader.loadClass(className).kotlin

    val keepFieldType = generatedClass.declaredMemberProperties.single { it.name == "keepFieldType" }
    assertEquals(clas, keepFieldType.returnType.classifier)

    val lazyClassKeyName = generatedClass.declaredMemberProperties.single { it.name == "lazyClassKeyName" }
    assertFalse(lazyClassKeyName.isConst)
    assertEquals(typeOf<String>(), lazyClassKeyName.returnType)
    assertEquals(clas.qualifiedName, lazyClassKeyName.call())

    val simpleName = classUnderTest.replace('.', '_')
    val proguardFileName = "${simpleName}BindingsModule_LazyClassKeys"
    val anvilFolder = File(outputDirectory.parentFile, "ksp/sources/resources/META-INF/proguard")
    val generatedProFile = File(anvilFolder, "$proguardFileName.pro")
    assertTrue(generatedProFile.exists())

    val expectedProguardContent = "-keep,allowobfuscation,allowshrinking class ${clas.qualifiedName}"
    val actualProguardContent = generatedProFile.readText()
    assertEquals(expectedProguardContent, actualProguardContent)
}

/**
 * Simulates and validates the Gradle plugin's file copying behavior.
 *
 * In a real Gradle build, the Whetstone plugin copies .pro files from Anvil's output
 * to kotlin-classes/META-INF/proguard/ for AGP to discover and package into AARs.
 *
 * This function:
 * 1. Simulates that copying by manually doing it in the test environment
 * 2. Validates the files exist in the correct location with correct content
 *
 * Note: This is a simulation test, not an integration test of the actual Gradle plugin.
 * The actual Gradle plugin behavior is tested via AAR verification in integration tests.
 */
internal fun JvmCompilationResult.simulateAndValidateGradlePluginCopy(
    classUnderTest: String
) {
    val clas = classLoader.loadClass(classUnderTest).kotlin
    val simpleName = classUnderTest.replace('.', '_')
    val proguardFileName = "${simpleName}BindingsModule_LazyClassKeys"

    // Source: where Anvil generates the file
    val anvilFolder = File(outputDirectory.parentFile, "ksp/sources/resources/META-INF/proguard")
    val sourceProFile = File(anvilFolder, "$proguardFileName.pro")

    // Destination: where Gradle plugin should copy it (simulating kotlin-classes directory)
    val kotlinClassesFolder = File(outputDirectory, "META-INF/proguard")
    val copiedProFile = File(kotlinClassesFolder, "$proguardFileName.pro")

    // Manually copy to simulate what Gradle plugin does (since we're in unit test, not full Gradle build)
    if (sourceProFile.exists()) {
        kotlinClassesFolder.mkdirs()
        sourceProFile.copyTo(copiedProFile, overwrite = true)
    }

    // Verify the file exists in kotlin-classes location
    assertTrue(
        copiedProFile.exists(),
        "Proguard file should be copied to kotlin-classes at ${copiedProFile.absolutePath}"
    )

    // Verify content matches
    val expectedProguardContent = "-keep,allowobfuscation,allowshrinking class ${clas.qualifiedName}"
    val actualProguardContent = copiedProFile.readText()
    assertEquals(
        expectedProguardContent,
        actualProguardContent,
        "Copied proguard file content should match original"
    )
}

internal fun JvmCompilationResult.validateNoLazyBindingKey(classUnderTest: String) {
    val className = "${classUnderTest}BindingsModule_Binds_LazyMapKey"
    assertFailsWith<ClassNotFoundException> { classLoader.loadClass(className).kotlin }
}

internal fun JvmCompilationResult.validateInjectorBinding(classUnderTest: String, scope: KClass<*>) {
    val clas = classLoader.loadClass(classUnderTest).kotlin
    val bindingModule = classLoader.loadClass("${classUnderTest}BindingsModule").kotlin
    val membersInjector = MembersInjector::class.asClassName()

    assertTrue(bindingModule.hasAnnotation<Module>())
    assertEquals(scope, bindingModule.findAnnotation<ContributesTo>()?.scope)

    val bindsMethod = bindingModule.declaredMemberFunctions.single { it.name == "binds" }
    assertTrue(bindsMethod.hasAnnotation<Binds>())
    assertTrue(bindsMethod.hasAnnotation<IntoMap>())
    assertEquals(clas, bindsMethod.findAnnotation<LazyClassKey>()?.value)
    assertEquals(
        membersInjector.parameterizedBy(clas.asClassName()),
        bindsMethod.findParameterByName("target")?.type?.asTypeName()
    )
    assertEquals(membersInjector.parameterizedBy(STAR), bindsMethod.returnType.asTypeName())
}

internal fun JvmCompilationResult.validateAppComponent(packageName: String = "") {
    val qualifiedName = if (packageName.isEmpty()) "GeneratedApplicationComponent" else "$packageName.GeneratedApplicationComponent"
    val appComponent = classLoader.loadClass(qualifiedName).kotlin

    assertTrue(appComponent.isAbstract)
    assertTrue(appComponent.hasAnnotation<Singleton>())
    assertEquals(ApplicationScope::class, appComponent.findAnnotation<SingleIn>()?.value)
    assertEquals(ApplicationScope::class, appComponent.findAnnotation<MergeComponent>()?.scope)

    val appComponentFactoryCn = appComponent.asClassName().nestedClass("Factory")
    val appComponentFactory = classLoader.loadClass(appComponentFactoryCn.reflectionName()).kotlin
    val appComponentCompanionCn = appComponent.asClassName().nestedClass("Default")
    val appComponentCompanion = classLoader.loadClass(appComponentCompanionCn.reflectionName()).kotlin

    assertTrue(appComponentFactory.hasAnnotation<MergeComponent.Factory>())
    assertTrue(appComponentFactory.superclasses.contains(ApplicationComponent.Factory::class))
    assertTrue(appComponentCompanion.isCompanion)
    assertTrue(appComponentCompanion.superclasses.contains(appComponentFactory))
}
