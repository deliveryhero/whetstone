package com.deliveryhero.whetstone.compiler.handlers

import com.deliveryhero.whetstone.compiler.FqNames
import com.deliveryhero.whetstone.compiler.classNameArgument
import com.google.devtools.ksp.symbol.KSAnnotation
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import dagger.MembersInjector

internal class ExplicitInjectorModuleInfoProvider : ModuleInfoProvider {
    private val membersInjectorCn = MembersInjector::class.asClassName()

    override val supportedAnnotation = FqNames.CONTRIBUTES_INJECTOR

    override fun getScope(annotation: KSAnnotation): ClassName = annotation.classNameArgument("scope")

    override fun getTarget(annotatedClass: ClassName) = membersInjectorCn.parameterizedBy(annotatedClass)

    override fun getOutput(annotatedClass: ClassName) = membersInjectorCn.parameterizedBy(STAR)
}

internal class InjectorModuleInfoProvider(
    override val supportedAnnotation: String,
    private val scopeCn: ClassName,
) : ModuleInfoProvider {
    private val membersInjectorCn = MembersInjector::class.asClassName()

    override fun getScope(annotation: KSAnnotation) = scopeCn

    override fun getTarget(annotatedClass: ClassName) = membersInjectorCn.parameterizedBy(annotatedClass)

    override fun getOutput(annotatedClass: ClassName) = membersInjectorCn.parameterizedBy(STAR)
}

internal class InstanceModuleInfoProvider(
    override val supportedAnnotation: String,
    private val scopeCn: ClassName,
    private val baseClass: TypeName
) : ModuleInfoProvider {

    override fun getScope(annotation: KSAnnotation) = scopeCn

    override fun getTarget(annotatedClass: ClassName) = annotatedClass

    override fun getOutput(annotatedClass: ClassName) = baseClass
}
