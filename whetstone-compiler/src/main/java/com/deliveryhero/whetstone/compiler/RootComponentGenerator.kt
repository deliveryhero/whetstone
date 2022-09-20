package com.deliveryhero.whetstone.compiler

import com.google.auto.service.AutoService
import com.squareup.anvil.annotations.MergeComponent
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.CodeGenerator
import com.squareup.anvil.compiler.api.GeneratedFile
import com.squareup.anvil.compiler.api.createGeneratedFile
import com.squareup.anvil.compiler.internal.asClassName
import com.squareup.anvil.compiler.internal.buildFile
import com.squareup.anvil.compiler.internal.reference.ClassReference
import com.squareup.anvil.compiler.internal.reference.classAndInnerClassReferences
import com.squareup.anvil.compiler.internal.safePackageString
import com.squareup.kotlinpoet.*
import dagger.Component
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import javax.inject.Singleton

@AutoService(CodeGenerator::class)
public class RootComponentGenerator : CodeGenerator {

    override fun isApplicable(context: AnvilContext): Boolean = true

    override fun generateCode(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>
    ): Collection<GeneratedFile> {
        return projectFiles
            .classAndInnerClassReferences(module)
            .mapNotNull { clas ->
                if (!shouldGenerateRoot(clas)) return@mapNotNull null
                val info = generateRoot(module, clas)
                createGeneratedFile(codeGenDir, info.packageName, info.fileName, info.content)
            }.toList()
    }

    private fun shouldGenerateRoot(clas: ClassReference): Boolean {
        val contributesApp = clas.annotations.find { it.fqName == FqNames.CONTRIBUTES_APP }
        return contributesApp?.getValue<Boolean>("generateAppComponent", 0) == true
    }

    private fun generateRoot(module: ModuleDescriptor, clas: ClassReference): GeneratedFileInfo {
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
        val typeSpec = TypeSpec.interfaceBuilder(generatedComponentCn)
            .addSuperinterface(applicationComponentCn)
            .addAnnotation(mergeComponent)
            .addAnnotation(singleIn)
            .addAnnotation(Singleton::class)
            .addType(
                TypeSpec.interfaceBuilder(generatedComponentCn.nestedClass("Factory"))
                    .addSuperinterface(applicationComponentCn.nestedClass("Factory"))
                    .addAnnotation(Component.Factory::class)
                    .build()
            )
            .addType(
                TypeSpec.companionObjectBuilder("Default")
                    .addSuperinterface(
                        generatedComponentCn.nestedClass("Factory"),
                        CodeBlock.of(
                            "%T.factory()",
                            generatedComponentCn.peerClass("Dagger${generatedComponentCn.simpleName}")
                        )
                    )
                    .build()
            )
            .build()

        addType(typeSpec)
    }
}
