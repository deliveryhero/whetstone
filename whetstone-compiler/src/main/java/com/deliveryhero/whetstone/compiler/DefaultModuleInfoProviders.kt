package com.deliveryhero.whetstone.compiler

import com.squareup.anvil.compiler.internal.asClassName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import dagger.MembersInjector
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotationEntry

internal class InjectorModuleInfoProvider : ModuleInfoProvider {
    private val membersInjectorCn = MembersInjector::class.asClassName()

    override val supportedAnnotation = FqNames.CONTRIBUTES_INJECTOR

    override fun getScope(annotation: KtAnnotationEntry, module: ModuleDescriptor): ClassName {
        return annotation.scope(module).asClassName(module)
    }

    override fun getTarget(annotatedClass: ClassName) = membersInjectorCn.parameterizedBy(annotatedClass)

    override fun getOutput(annotatedClass: ClassName) = membersInjectorCn.parameterizedBy(STAR)
}

internal class AutoInjectorModuleInfoProvider(private val scopeCn: ClassName) : ModuleInfoProvider {
    private val membersInjectorCn = MembersInjector::class.asClassName()

    override val supportedAnnotation = FqNames.CONTRIBUTES_ANDROID_BINDING

    override fun getScope(annotation: KtAnnotationEntry, module: ModuleDescriptor) = scopeCn

    override fun getTarget(annotatedClass: ClassName) = membersInjectorCn.parameterizedBy(annotatedClass)

    override fun getOutput(annotatedClass: ClassName) = membersInjectorCn.parameterizedBy(STAR)
}

internal class InstanceModuleInfoProvider(
    override val supportedAnnotation: FqName,
    private val scopeCn: ClassName,
    private val baseClass: TypeName
) : ModuleInfoProvider {

    override fun getScope(annotation: KtAnnotationEntry, module: ModuleDescriptor) = scopeCn

    override fun getTarget(annotatedClass: ClassName) = annotatedClass

    override fun getOutput(annotatedClass: ClassName) = baseClass
}
