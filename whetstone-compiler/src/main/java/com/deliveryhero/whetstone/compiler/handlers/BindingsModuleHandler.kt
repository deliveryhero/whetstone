package com.deliveryhero.whetstone.compiler.handlers

import com.deliveryhero.whetstone.compiler.CodegenHandler
import com.deliveryhero.whetstone.compiler.FqNames
import com.deliveryhero.whetstone.compiler.GeneratedFileInfo
import com.deliveryhero.whetstone.compiler.GeneratedFileType
import com.deliveryhero.whetstone.compiler.classNameArgument
import com.deliveryhero.whetstone.compiler.fqName
import com.deliveryhero.whetstone.compiler.metaAnnotations
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import dagger.Binds
import dagger.Module
import dagger.internal.IdentifierNameString
import dagger.internal.KeepFieldType
import dagger.multibindings.IntoMap
import dagger.multibindings.LazyClassKey

private const val PROGUARD_KEEP_RULE = "-keep,allowobfuscation,allowshrinking class"

internal class BindingsModuleHandler(private val generateFactories: Boolean) : CodegenHandler {

    private val dynamicProviderMap = hashMapOf<String, ModuleInfoProvider?>().apply {
        val injectorModule = ExplicitInjectorModuleInfoProvider()
        put(injectorModule.supportedAnnotation, injectorModule)
    }

    override fun processClass(clas: KSClassDeclaration): Collection<GeneratedFileInfo> {
        val provider = findProvider(clas) ?: return emptyList()
        return generateModule(provider, clas)
    }

    private fun findProvider(clas: KSClassDeclaration): ModuleInfoProvider? {
        var result: ModuleInfoProvider? = null
        for (annotation in clas.annotations) {
            val annotationFqName = annotation.fqName() ?: continue
            dynamicProviderMap.getOrPutNullable(annotationFqName) {
                for (meta in annotation.metaAnnotations()) {
                    when (meta.fqName()) {
                        FqNames.AUTO_INSTANCE -> {
                            val base = meta.classNameArgument("base")
                            val scope = meta.classNameArgument("scope")
                            return@getOrPutNullable InstanceModuleInfoProvider(annotationFqName, scope, base)
                        }
                        FqNames.AUTO_INJECTOR -> {
                            val scope = meta.classNameArgument("scope")
                            return@getOrPutNullable InjectorModuleInfoProvider(annotationFqName, scope)
                        }
                    }
                }
                // Unsupported annotation
                null
            }?.let { provider ->
                require(result == null) {
                    "Found more than 1 Contributes* annotation in class '${clas.qualifiedName?.asString()}'"
                }
                result = provider
            }
        }
        return result
    }

    private fun generateModule(provider: ModuleInfoProvider, clas: KSClassDeclaration): List<GeneratedFileInfo> {
        val className = clas.toClassName()
        val packageName = className.packageName
        val outputFileName = className.simpleName + "BindingsModule"

        val annotation = clas.annotations.single { it.fqName() == provider.supportedAnnotation }
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

        val moduleInterfaceSpec = TypeSpec.interfaceBuilder(outputFileName)
            .addAnnotation(Module::class)
            .addAnnotation(contributesToAnnotation)
            .addFunction(bindsFunction)
            .build()

        val content = FileSpec.builder(packageName, outputFileName)
            .addFileComment("Automatically generated file. DO NOT MODIFY!")
            .addType(moduleInterfaceSpec)
            .build()
            .toString()

        val generatedFiles = mutableListOf<GeneratedFileInfo>()

        generatedFiles += GeneratedFileInfo(
            packageName = packageName,
            fileName = outputFileName,
            content = content,
            sourceFiles = setOfNotNull(clas.containingFile),
            fileType = GeneratedFileType.KOTLIN,
        )

        if (generateFactories) {
            // Whetstone is explicitly generating this extra type to properly support
            // Dagger's LazyClassKey. Ideally, this should be handled by Anvil, but that
            // isn't happening now, so until then, we'll maintain this workaround
            val lazyMapKeyContent = FileSpec.builder(packageName, outputFileName)
                .addFileComment("Automatically generated file. DO NOT MODIFY!")
                .addType(generateLazyClassKey(outputFileName, className))
                .build()
                .toString()

            generatedFiles += GeneratedFileInfo(
                packageName = packageName,
                fileName = "${outputFileName}_Binds_LazyMapKey",
                content = lazyMapKeyContent,
                sourceFiles = setOfNotNull(clas.containingFile),
                fileType = GeneratedFileType.KOTLIN,
            )

            generatedFiles += generatedProguardFileInfo(
                outputFileName = "${packageName}_$outputFileName",
                className = className,
                sourceFile = clas.containingFile,
            )
        }

        return generatedFiles
    }

    private fun generatedProguardFileInfo(
        outputFileName: String,
        className: ClassName,
        sourceFile: KSFile?,
    ): GeneratedFileInfo {
        val proguardRuleContent = "$PROGUARD_KEEP_RULE ${className.canonicalName}"

        // The file must be placed in "META-INF/proguard/" for AGP to find it.
        // We can use the `packageName` property of GeneratedFileInfo to represent the path.
        return GeneratedFileInfo(
            packageName = "META-INF/proguard",
            fileName = "${outputFileName}_LazyClassKeys".replace('.', '_'),
            content = proguardRuleContent,
            sourceFiles = setOfNotNull(sourceFile),
            fileType = GeneratedFileType.PROGUARD,
        )
    }

    private fun generateLazyClassKey(outputFileName: String, className: ClassName): TypeSpec {
        val keepFieldType = PropertySpec.builder("keepFieldType", className.copy(nullable = true))
            .addAnnotation(JvmField::class)
            .addAnnotation(KeepFieldType::class)
            .initializer("null")
            .build()
        val lazyClassKeyName = PropertySpec.builder("lazyClassKeyName", STRING)
            .addAnnotation(JvmField::class)
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
}
