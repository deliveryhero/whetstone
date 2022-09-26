package com.deliveryhero.whetstone.compiler.handlers

import com.deliveryhero.whetstone.compiler.CodegenHandler
import com.deliveryhero.whetstone.compiler.FqNames
import com.deliveryhero.whetstone.compiler.GeneratedFileInfo
import com.deliveryhero.whetstone.compiler.getValue
import com.squareup.anvil.annotations.MergeComponent
import com.squareup.anvil.compiler.internal.asClassName
import com.squareup.anvil.compiler.internal.buildFile
import com.squareup.anvil.compiler.internal.reference.ClassReference
import com.squareup.anvil.compiler.internal.safePackageString
import com.squareup.kotlinpoet.*
import dagger.Component
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import javax.inject.Singleton

internal class AppComponentHandler : CodegenHandler {

    override fun processClass(clas: ClassReference, module: ModuleDescriptor): GeneratedFileInfo? {
        if (!shouldGenerateRoot(clas)) return null
        return generateAppComponent(module, clas)
    }

    private fun shouldGenerateRoot(clas: ClassReference): Boolean {
        val contributesApp = clas.annotations.find { it.fqName == FqNames.CONTRIBUTES_APP }
        return contributesApp?.getValue<Boolean>("generateAppComponent", 0) == true
    }

    private fun generateAppComponent(module: ModuleDescriptor, clas: ClassReference): GeneratedFileInfo {
        val packageName = clas.packageFqName.safePackageString(
            dotPrefix = false,
            dotSuffix = false,
        )
        val outputFileName = "GeneratedApplicationComponent"

        val content = FileSpec.buildFile(
            packageName = packageName,
            fileName = outputFileName,
            generatorComment = "Automatically generated file. DO NOT MODIFY"
        ) {
            writeAppComponent(module, packageName, outputFileName)
        }

        return GeneratedFileInfo(packageName, outputFileName, content)
    }

    private fun FileSpec.Builder.writeAppComponent(module: ModuleDescriptor, packageName: String, className: String) {
        val applicationComponentCn = FqNames.APPLICATION_COMPONENT.asClassName(module)
        val applicationScopeCn = FqNames.APPLICATION_SCOPE.asClassName(module)
        val generatedComponentCn = ClassName(packageName, className)

        val mergeComponent = AnnotationSpec.builder(MergeComponent::class)
            .addMember("%T::class", applicationScopeCn)
            .build()
        val singleIn = AnnotationSpec.builder(FqNames.SINGLE_IN.asClassName(module))
            .addMember("%T::class", applicationScopeCn)
            .build()
        val factorySpec = TypeSpec.interfaceBuilder(generatedComponentCn.nestedClass("Factory"))
            .addSuperinterface(applicationComponentCn.nestedClass("Factory"))
            .addAnnotation(Component.Factory::class)
            .build()
        val companionObjectSpec = TypeSpec.companionObjectBuilder("Default")
            .addSuperinterface(
                generatedComponentCn.nestedClass("Factory"),
                CodeBlock.of(
                    "%T.factory()",
                    generatedComponentCn.peerClass("Dagger${generatedComponentCn.simpleName}")
                )
            )
            .build()

        val appComponentSpec = TypeSpec.interfaceBuilder(generatedComponentCn)
            .addSuperinterface(applicationComponentCn)
            .addAnnotation(mergeComponent)
            .addAnnotation(singleIn)
            .addAnnotation(Singleton::class)
            .addType(factorySpec)
            .addType(companionObjectSpec)
            .build()

        addType(appComponentSpec)
    }
}
