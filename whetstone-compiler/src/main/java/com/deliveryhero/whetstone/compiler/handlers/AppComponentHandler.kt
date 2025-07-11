package com.deliveryhero.whetstone.compiler.handlers

import com.deliveryhero.whetstone.compiler.CodegenHandler
import com.deliveryhero.whetstone.compiler.FqNames
import com.deliveryhero.whetstone.compiler.FqNames.APPLICATION
import com.deliveryhero.whetstone.compiler.GeneratedFileInfo
import com.deliveryhero.whetstone.compiler.GeneratedFileType
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

    override fun processClass(clas: ClassReference, module: ModuleDescriptor): Collection<GeneratedFileInfo> {
        if (!shouldGenerateRoot(clas)) return emptyList()
        return listOf(generateAppComponent(module, clas))
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

        return GeneratedFileInfo(
            packageName = packageName,
            fileName = outputFileName,
            content = content,
            sourceFiles = setOf(clas.containingFileAsJavaFile),
            fileType = GeneratedFileType.KOTLIN,
        )
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
        val appParam = ParameterSpec.builder("application", APPLICATION.asClassName(module))
            .build()
        val companionObjectSpec = TypeSpec.companionObjectBuilder("Default")
            .addSuperinterface(generatedComponentCn.nestedClass("Factory"))
            .addFunction(
                FunSpec.builder("create")
                    .addParameter(appParam)
                    .returns(applicationComponentCn)
                    .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                    .addCode(CodeBlock.of(
                        "return DaggerGeneratedApplicationComponent.factory().create(%L)",
                        appParam.name
                    ))
                    .build())
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
