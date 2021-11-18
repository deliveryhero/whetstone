package com.deliveryhero.whetstone.compiler

import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.compiler.internal.asClassName
import com.squareup.anvil.compiler.internal.buildFile
import com.squareup.anvil.compiler.internal.requireAnnotation
import com.squareup.anvil.compiler.internal.safePackageString
import com.squareup.kotlinpoet.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtClassOrObject

internal class BindingModuleTypeWriter {

    fun createNew(provider: ModuleInfoProvider, clas: KtClassOrObject, module: ModuleDescriptor): GeneratedFileInfo {
        val className = clas.asClassName()
        val packageName = clas.containingKtFile.packageFqName.safePackageString(
            dotPrefix = false,
            dotSuffix = false,
        )
        val outputFileName = className.simpleName + "BindingsModule"

        val annotation = clas.requireAnnotation(provider.supportedAnnotation, module)
        val componentScopeCn = provider.getScope(annotation, module)
        val contributesToAnnotation = AnnotationSpec.builder(ContributesTo::class)
            .addMember("%T::class", componentScopeCn)
            .build()

        val classKeyCn = provider.getMultibindingKey()
        val classKeyAnnotation = AnnotationSpec.builder(classKeyCn)
            .addMember("%T::class", className)
            .build()

        val bindsFunction = FunSpec.builder("binds")
            .addParameter("target", provider.getTarget(className))
            .returns(provider.getOutput(className))
            .addModifiers(KModifier.ABSTRACT)
            .addAnnotation(Binds::class)
            .addAnnotation(IntoMap::class)
            .addAnnotation(classKeyAnnotation)
            .build()

        val content = FileSpec.buildFile(packageName, outputFileName) {
            val moduleInterfaceSpec = TypeSpec.interfaceBuilder(outputFileName)
                .addAnnotation(Module::class)
                .addAnnotation(contributesToAnnotation)
                .addFunction(bindsFunction)
                .build()

            addType(moduleInterfaceSpec)
        }

        return GeneratedFileInfo(packageName, outputFileName, content)
    }
}
