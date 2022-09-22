package com.deliveryhero.whetstone.compiler.handlers

import com.deliveryhero.whetstone.compiler.CodegenHandler
import com.deliveryhero.whetstone.compiler.FqNames
import com.deliveryhero.whetstone.compiler.GeneratedFileInfo
import com.deliveryhero.whetstone.compiler.getValue
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.compiler.internal.buildFile
import com.squareup.anvil.compiler.internal.reference.AnnotationReference
import com.squareup.anvil.compiler.internal.reference.ClassReference
import com.squareup.anvil.compiler.internal.reference.asClassName
import com.squareup.anvil.compiler.internal.safePackageString
import com.squareup.kotlinpoet.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.FqName

internal class BindingsModuleHandler : CodegenHandler {

    private val dynamicProviderMap = hashMapOf<FqName, ModuleInfoProvider?>().apply {
        val injectorModule = ExplicitInjectorModuleInfoProvider()
        put(injectorModule.supportedAnnotation, injectorModule)
    }

    override fun processClass(clas: ClassReference, module: ModuleDescriptor): GeneratedFileInfo? {
        val provider = findProvider(clas) ?: return null
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

    private fun generateModule(provider: ModuleInfoProvider, clas: ClassReference): GeneratedFileInfo {
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

    private fun AnnotationReference.getAsClassName(name: String, index: Int): ClassName {
        return getValue<ClassReference>(name, index).asClassName()
    }
}
