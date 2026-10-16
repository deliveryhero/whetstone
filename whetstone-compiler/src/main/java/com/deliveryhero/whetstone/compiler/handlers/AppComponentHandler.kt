package com.deliveryhero.whetstone.compiler.handlers

import com.deliveryhero.whetstone.compiler.CodegenHandler
import com.deliveryhero.whetstone.compiler.FqNames
import com.deliveryhero.whetstone.compiler.GeneratedFileInfo
import com.deliveryhero.whetstone.compiler.GeneratedFileType
import com.deliveryhero.whetstone.compiler.argumentValue
import com.deliveryhero.whetstone.compiler.fqName
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.anvil.annotations.MergeComponent
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import javax.inject.Singleton

internal class AppComponentHandler : CodegenHandler {

    override fun processClass(clas: KSClassDeclaration): Collection<GeneratedFileInfo> {
        if (!shouldGenerateRoot(clas)) return emptyList()
        return listOf(generateAppComponent(clas))
    }

    private fun shouldGenerateRoot(clas: KSClassDeclaration): Boolean {
        val contributesApp = clas.annotations.find { it.fqName() == FqNames.CONTRIBUTES_APP }
        return contributesApp?.argumentValue<Boolean>("generateAppComponent") == true
    }

    private fun generateAppComponent(clas: KSClassDeclaration): GeneratedFileInfo {
        val packageName = clas.packageName.asString()
        val outputFileName = "GeneratedApplicationComponent"
        val generatedComponentCn = ClassName(packageName, outputFileName)

        val content = FileSpec.builder(packageName, outputFileName)
            .addFileComment("Automatically generated file. DO NOT MODIFY")
            .addType(buildAppComponentSpec(generatedComponentCn))
            .build()
            .toString()

        return GeneratedFileInfo(
            packageName = packageName,
            fileName = outputFileName,
            content = content,
            sourceFiles = setOfNotNull(clas.containingFile),
            fileType = GeneratedFileType.KOTLIN,
        )
    }

    private fun buildAppComponentSpec(generatedComponentCn: ClassName): TypeSpec {
        val mergeComponent = AnnotationSpec.builder(MergeComponent::class)
            .addMember("%T::class", FqNames.APPLICATION_SCOPE_CN)
            .build()
        val singleIn = AnnotationSpec.builder(FqNames.SINGLE_IN_CN)
            .addMember("%T::class", FqNames.APPLICATION_SCOPE_CN)
            .build()
        val factorySpec = TypeSpec.interfaceBuilder(generatedComponentCn.nestedClass("Factory"))
            .addSuperinterface(FqNames.APPLICATION_COMPONENT_CN.nestedClass("Factory"))
            .addAnnotation(MergeComponent.Factory::class)
            .build()
        val appParam = ParameterSpec.builder("application", FqNames.APPLICATION_CN).build()
        val companionObjectSpec = TypeSpec.companionObjectBuilder("Default")
            .addSuperinterface(generatedComponentCn.nestedClass("Factory"))
            .addFunction(
                FunSpec.builder("create")
                    .addParameter(appParam)
                    .returns(FqNames.APPLICATION_COMPONENT_CN)
                    .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                    .addCode(
                        CodeBlock.of(
                            "return DaggerGeneratedApplicationComponent.factory().create(%L)",
                            appParam.name
                        )
                    )
                    .build()
            )
            .build()

        return TypeSpec.interfaceBuilder(generatedComponentCn)
            .addSuperinterface(FqNames.APPLICATION_COMPONENT_CN)
            .addAnnotation(mergeComponent)
            .addAnnotation(singleIn)
            .addAnnotation(Singleton::class)
            .addType(factorySpec)
            .addType(companionObjectSpec)
            .build()
    }
}
