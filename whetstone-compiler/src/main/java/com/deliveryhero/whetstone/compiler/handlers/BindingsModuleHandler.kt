package com.deliveryhero.whetstone.compiler.handlers

import com.deliveryhero.whetstone.compiler.CodegenHandler
import com.deliveryhero.whetstone.compiler.FqNames
import com.deliveryhero.whetstone.compiler.GeneratedFileInfo
import com.deliveryhero.whetstone.compiler.GeneratedFileType
import com.deliveryhero.whetstone.compiler.getValue
import com.squareup.anvil.compiler.internal.buildFile
import com.squareup.anvil.compiler.internal.reference.AnnotationReference
import com.squareup.anvil.compiler.internal.reference.ClassReference
import com.squareup.anvil.compiler.internal.reference.asClassName
import com.squareup.anvil.compiler.internal.safePackageString
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.IntoMap
import dev.zacsweers.metro.ClassKey
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.FqName

internal class BindingsModuleHandler(private val generateFactories: Boolean) : CodegenHandler {

    private val dynamicProviderMap = hashMapOf<FqName, ModuleInfoProvider?>().apply {
        val injectorModule = ExplicitInjectorModuleInfoProvider()
        put(injectorModule.supportedAnnotation, injectorModule)
    }

    override fun processClass(clas: ClassReference, module: ModuleDescriptor): Collection<GeneratedFileInfo> {
        val provider = findProvider(clas) ?: return emptyList()
        return generateModule(provider, clas)
    }

    private fun findProvider(clas: ClassReference): ModuleInfoProvider? {
        var result: ModuleInfoProvider? = null
        for (annotation in clas.annotations) {
            val annotationFqName = annotation.fqName
            dynamicProviderMap.getOrPutNullable(annotationFqName) {
                for (meta in annotation.classReference.annotations) {
                    when (meta.fqName) {
                        FqNames.AUTO_INSTANCE -> {
                            val base = meta.getAsClassName("base", 0)
                            val scope = meta.getAsClassName("scope", 1)
                            return@getOrPutNullable InstanceModuleInfoProvider(annotationFqName, scope, base)
                        }
                        FqNames.AUTO_INJECTOR -> {
                            val scope = meta.getAsClassName("scope", 0)
                            return@getOrPutNullable InjectorModuleInfoProvider(annotationFqName, scope)
                        }
                    }
                }
                // Unsupported annotation
                null
            }?.let { provider ->
                require(result == null) {
                    "Found more than 1 Contributes* annotation in class '${clas.fqName}'"
                }
                result = provider
            }
        }
        return result
    }

    private fun generateModule(provider: ModuleInfoProvider, clas: ClassReference): List<GeneratedFileInfo> {
        val className = clas.asClassName()
        val packageName = clas.packageFqName.safePackageString(
            dotPrefix = false,
            dotSuffix = false,
        )
        val outputFileName = className.simpleName + "BindingsModule"

        val annotation = clas.annotations.single { it.fqName == provider.supportedAnnotation }
        val componentScopeCn = provider.getScope(annotation)
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

        val content = FileSpec.buildFile(
            packageName = packageName,
            fileName = outputFileName,
            generatorComment = "Automatically generated file. DO NOT MODIFY!"
        ) {
            val moduleInterfaceSpec = TypeSpec.interfaceBuilder(outputFileName)
                .addAnnotation(BindingContainer::class)
                .addAnnotation(contributesToAnnotation)
                .addFunction(bindsFunction)
                .build()

            addType(moduleInterfaceSpec)
        }

        val generatedFiles = mutableListOf<GeneratedFileInfo>()

        generatedFiles += GeneratedFileInfo(
            packageName = packageName,
            fileName = outputFileName,
            content = content,
            sourceFiles = setOf(clas.containingFileAsJavaFile),
            fileType = GeneratedFileType.KOTLIN,
        )

        return generatedFiles
    }

    private fun <K, V> MutableMap<K, V?>.getOrPutNullable(key: K, func: () -> V?): V? {
        return if (key in this) get(key) else func().also { put(key, it) }
    }

    private fun AnnotationReference.getAsClassName(name: String, index: Int): ClassName {
        return getValue<ClassReference>(name, index).asClassName()
    }
}
