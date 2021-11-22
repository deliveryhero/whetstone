package com.deliveryhero.whetstone.codegen

import com.squareup.anvil.annotations.ContributesTo
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.tschuchort.compiletesting.KotlinCompilation
import dagger.Binds
import dagger.MembersInjector
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.hasAnnotation
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
