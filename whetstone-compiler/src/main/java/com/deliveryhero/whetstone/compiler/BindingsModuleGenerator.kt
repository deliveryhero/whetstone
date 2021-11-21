package com.deliveryhero.whetstone.compiler

import com.google.auto.service.AutoService
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.CodeGenerator
import com.squareup.anvil.compiler.api.GeneratedFile
import com.squareup.anvil.compiler.api.createGeneratedFile
import com.squareup.anvil.compiler.internal.annotationOrNull
import com.squareup.anvil.compiler.internal.classesAndInnerClass
import com.squareup.anvil.compiler.internal.fqNameOrNull
import com.squareup.anvil.compiler.internal.requireClassDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

@AutoService(CodeGenerator::class)
public class BindingsModuleGenerator : CodeGenerator {

    private val dynamicProviderMap = hashMapOf<FqName, ModuleInfoProvider?>().apply {
        val injectorModule = InjectorModuleInfoProvider()
        put(injectorModule.supportedAnnotation, injectorModule)
    }
    private val meta = FqName("com.deliveryhero.whetstone.AutoScopedBinding")
    private val typeWriter = BindingModuleTypeWriter()

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
                val info = typeWriter.createNew(provider, clas, module)
                createGeneratedFile(codeGenDir, info.packageName, info.fileName, info.content)
            }.toList()
    }

    private fun findProvider(clas: KtClassOrObject, module: ModuleDescriptor): ModuleInfoProvider? {
        var result: ModuleInfoProvider? = null
        for (annotation in clas.annotationEntries) {
            val annotationFqName = annotation.fqNameOrNull(module) ?: continue
            dynamicProviderMap.getOrPutNullable(annotationFqName) {
                val metaInfo = annotationFqName.requireClassDescriptor(module).annotationOrNull(meta)
                    ?: return@getOrPutNullable null
                val base = metaInfo.getValueAsClassName("base", module)
                val scope = metaInfo.getValueAsClassName("scope", module)
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

    private fun <K, V> MutableMap<K, V?>.getOrPutNullable(key: K, func: () -> V?): V? {
        return if (key in this) get(key) else func().also { put(key, it) }
    }
}
