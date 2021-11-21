package com.deliveryhero.whetstone.codegen

import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.compiler.internal.testing.compileAnvil
import com.squareup.anvil.compiler.internal.testing.getValue
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
import org.intellij.lang.annotations.Language
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.javaMethod
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal fun compileWhetstone(
    @Language("kotlin") source1: String,
    @Language("kotlin") source2: String,
    validator: KotlinCompilation.Result.() -> Unit
) {
    compileAnvil(source1, block = validator)
    compileAnvil(source2, block = validator)
}

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
