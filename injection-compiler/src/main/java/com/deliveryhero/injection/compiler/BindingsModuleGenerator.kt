package com.deliveryhero.injection.compiler

import com.google.auto.service.AutoService
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.CodeGenerator
import com.squareup.anvil.compiler.api.GeneratedFile
import com.squareup.anvil.compiler.api.createGeneratedFile
import com.squareup.anvil.compiler.internal.*
import com.squareup.kotlinpoet.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

@AutoService(CodeGenerator::class)
public class BindingsModuleGenerator : CodeGenerator {

    private val providers = createDefaultProviders()

    override fun isApplicable(context: AnvilContext): Boolean = true

    override fun generateCode(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>
    ): Collection<GeneratedFile> {

        return projectFiles
            .classesAndInnerClass(module)
            .mapNotNull { clas ->
                val provider = getProvider(clas, module) ?: return@mapNotNull null
                val info = generateModule(provider, clas, module)
                createGeneratedFile(codeGenDir, info.packageName, info.fileName, info.content)
            }.toList()
    }

    private fun getProvider(clas: KtClassOrObject, module: ModuleDescriptor): ModuleInfoProvider? {
        var result: ModuleInfoProvider? = null
        for (annotation in clas.annotationEntries) {
            for (provider in providers) {
                val classAnnotation = annotation.requireFqName(module)
                if (classAnnotation == provider.supportedAnnotation) {
                    require(result == null) {
                        "Found more than 1 Contributes* annotation in class '${clas.fqName}'"
                    }
                    result = provider
                }
            }
        }
        return result
    }

    private fun generateModule(
        provider: ModuleInfoProvider,
        clas: KtClassOrObject,
        module: ModuleDescriptor
    ): GeneratedFileInfo {
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
