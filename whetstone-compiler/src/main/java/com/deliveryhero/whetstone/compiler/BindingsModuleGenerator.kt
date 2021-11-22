package com.deliveryhero.whetstone.compiler

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
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.constants.KClassValue
import java.io.File

@AutoService(CodeGenerator::class)
public class BindingsModuleGenerator : CodeGenerator {

    override fun isApplicable(context: AnvilContext): Boolean = true

    override fun generateCode(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>
    ): Collection<GeneratedFile> {

        return projectFiles
            .classesAndInnerClass(module)
            .mapNotNull { clas ->
                val provider = findProvider(clas, module) ?: return@mapNotNull null
                val info = generateModule(provider, clas, module)
                createGeneratedFile(codeGenDir, info.packageName, info.fileName, info.content)
            }.toList()
    }

    private val dynamicProviderMap = hashMapOf<FqName, ModuleInfoProvider?>().apply {
        val injectorModule = InjectorModuleInfoProvider()
        put(injectorModule.supportedAnnotation, injectorModule)
    }
    private val meta = FqName("com.deliveryhero.whetstone.AutoScopedBinding")

    private fun findProvider(clas: KtClassOrObject, module: ModuleDescriptor): ModuleInfoProvider? {
        var result: ModuleInfoProvider? = null
        for (annotation in clas.annotationEntries) {
            val annotationFqName = annotation.fqNameOrNull(module) ?: continue
            dynamicProviderMap.getOrPutNullable(annotationFqName) {
                val metaInfo = annotationFqName.requireClassDescriptor(module).annotationOrNull(meta)
                    ?: return@getOrPutNullable null
                val base = metaInfo.getValue("base", module)
                val scope = metaInfo.getValue("scope", module)
                InstanceModuleInfoProvider(annotationFqName, scope, base)
            }?.let { provider ->
                require(result == null) {
                    "Found more than 1 Contributes* annotation in class '${clas.fqName}'"
                }
                result = provider
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

        val classKeyAnnotation = AnnotationSpec.builder(ClassKey::class)
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

    private fun <K, V> MutableMap<K, V?>.getOrPutNullable(key: K, func: () -> V?): V? {
        return if (key in this) get(key) else func().also { put(key, it) }
    }

    private fun AnnotationDescriptor.getValue(name: String, module: ModuleDescriptor): ClassName {
        return (getAnnotationValue(name)?.value as KClassValue.Value.NormalClass)
            .classId.asSingleFqName()
            .asClassName(module)
    }
}
