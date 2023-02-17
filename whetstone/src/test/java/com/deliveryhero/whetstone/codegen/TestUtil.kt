@file:OptIn(ExperimentalCompilerApi::class)

package com.deliveryhero.whetstone.codegen

import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.app.ApplicationComponent
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.tschuchort.compiletesting.KotlinCompilation
import dagger.Binds
import dagger.Component
import dagger.MembersInjector
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import javax.inject.Singleton
import kotlin.reflect.KClass
import kotlin.reflect.full.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal fun KotlinCompilation.Result.validateInstanceBinding(
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
    assertEquals(clas, bindsMethod.findAnnotation<ClassKey>()?.value)
    assertEquals(clas, bindsMethod.findParameterByName("target")?.type?.classifier)
    assertEquals(baseClass, bindsMethod.returnType.classifier)
}

internal fun KotlinCompilation.Result.validateInjectorBinding(classUnderTest: String, scope: KClass<*>) {
    val clas = classLoader.loadClass(classUnderTest).kotlin
    val bindingModule = classLoader.loadClass("${classUnderTest}BindingsModule").kotlin
    val membersInjector = MembersInjector::class.asClassName()

    assertTrue(bindingModule.hasAnnotation<Module>())
    assertEquals(scope, bindingModule.findAnnotation<ContributesTo>()?.scope)

    val bindsMethod = bindingModule.declaredMemberFunctions.single { it.name == "binds" }
    assertTrue(bindsMethod.hasAnnotation<Binds>())
    assertTrue(bindsMethod.hasAnnotation<IntoMap>())
    assertEquals(clas, bindsMethod.findAnnotation<ClassKey>()?.value)
    assertEquals(
        membersInjector.parameterizedBy(clas.asClassName()),
        bindsMethod.findParameterByName("target")?.type?.asTypeName()
    )
    assertEquals(membersInjector.parameterizedBy(STAR), bindsMethod.returnType.asTypeName())
}

internal fun KotlinCompilation.Result.validateAppComponent() {
    val appComponent = classLoader.loadClass("GeneratedApplicationComponent").kotlin

    assertTrue(appComponent.isAbstract)
    assertTrue(appComponent.hasAnnotation<Singleton>())
    assertEquals(ApplicationScope::class, appComponent.findAnnotation<SingleIn>()?.value)
    assertEquals(ApplicationScope::class, appComponent.findAnnotation<MergeComponent>()?.scope)

    val appComponentFactoryCn = appComponent.asClassName().nestedClass("Factory")
    val appComponentFactory = classLoader.loadClass(appComponentFactoryCn.canonicalName).kotlin
    val appComponentCompanionCn = appComponent.asClassName().nestedClass("Default")
    val appComponentCompanion = classLoader.loadClass(appComponentCompanionCn.canonicalName).kotlin

    assertTrue(appComponentFactory.hasAnnotation<Component.Factory>())
    assertEquals(ApplicationComponent.Factory::class, appComponentFactory.superclasses.single())
    assertTrue(appComponentCompanion.isCompanion)
    assertEquals(appComponentFactory, appComponentFactory.superclasses.single())
}
