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
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import dagger.Binds
import dagger.Module
import dagger.internal.IdentifierNameString
import dagger.internal.KeepFieldType
import dagger.multibindings.IntoMap
import dagger.multibindings.LazyClassKey
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.FqName

private const val PROGUARD_KEEP_RULE = "-keep,allowshrinking,allowoptimization class"

internal class BindingsModuleHandler(private val generateFactories: Boolean) : CodegenHandler {

    private val dynamicProviderMap = hashMapOf<FqName, ModuleInfoProvider?>().apply {
        val injectorModule = ExplicitInjectorModuleInfoProvider()
        put(injectorModule.supportedAnnotation, injectorModule)
    }

    override fun processClass(
        clas: ClassReference,
        module: ModuleDescriptor
    ): Collection<GeneratedFileInfo> {
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

    private fun generateModule(
        provider: ModuleInfoProvider,
        clas: ClassReference
    ): List<GeneratedFileInfo> {
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

        val classKeyAnnotation = AnnotationSpec.builder(LazyClassKey::class)
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
            if (generateFactories) {
                // Whetstone is explicitly generating this extra type to properly support
                // Dagger's LazyClassKey. Ideally, this should be handled by Anvil, but that
                // isn't happening now, so until then, we'll maintain this workaround
                addType(generateLazyMapKey(outputFileName, className))
            }
        }
        val generatedFiles = mutableListOf<GeneratedFileInfo>()
        if (generateFactories) {
            val proguardFileInfo = generatedProguardFileInfo(outputFileName, className)
            generatedFiles += proguardFileInfo
        }

        generatedFiles += GeneratedFileInfo(
            packageName = packageName,
            fileName = outputFileName,
            content = content,
            sourceFile = clas.containingFileAsJavaFile
        )

        return generatedFiles
    }

    private fun generatedProguardFileInfo(
        outputFileName: String,
        className: ClassName
    ): GeneratedFileInfo {
        "${outputFileName}_Binds_LazyMapKey"
        // Generate the Proguard rule for the LazyMapKey helper.
        val proguardRuleContent = "$PROGUARD_KEEP_RULE ${className.canonicalName}"

        // The file must be placed in "META-INF/proguard/" for AGP to find it.
        // We can use the `packageName` property of GeneratedFileInfo to represent the path.
        val proguardFileInfo = GeneratedFileInfo(
            packageName = "../proguard",
            fileName = "${outputFileName}_Binds_LazyMapKey",
            content = proguardRuleContent,
            sourceFile = null
        )
        return proguardFileInfo
    }

    private fun generateLazyMapKey(outputFileName: String, className: ClassName): TypeSpec {
        val keepFieldType = PropertySpec.builder("keepFieldType", className.copy(nullable = true))
            .addAnnotation(JvmField::class)
            .addAnnotation(KeepFieldType::class)
            .initializer("null")
            .build()
        val lazyClassKeyName = PropertySpec.builder("lazyClassKeyName", STRING)
            .addModifiers(KModifier.CONST)
            .initializer("%S", className.canonicalName)
            .build()
        return TypeSpec.objectBuilder("${outputFileName}_Binds_LazyMapKey")
            .addAnnotation(IdentifierNameString::class)
            .addProperty(keepFieldType)
            .addProperty(lazyClassKeyName)
            .build()
    }

    private fun <K, V> MutableMap<K, V?>.getOrPutNullable(key: K, func: () -> V?): V? {
        return if (key in this) get(key) else func().also { put(key, it) }
    }

    private fun AnnotationReference.getAsClassName(name: String, index: Int): ClassName {
        return getValue<ClassReference>(name, index).asClassName()
    }
}
