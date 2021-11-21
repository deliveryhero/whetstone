package com.deliveryhero.whetstone.compiler

import com.google.auto.service.AutoService
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.CodeGenerator
import com.squareup.anvil.compiler.api.GeneratedFile
import com.squareup.anvil.compiler.api.createGeneratedFile
import com.squareup.anvil.compiler.internal.*
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

@AutoService(CodeGenerator::class)
public class AndroidBindingModuleGenerator : CodeGenerator {

    private val typeWriter = BindingModuleTypeWriter()
    private val dynamicProviderMap = hashMapOf<FqName, ModuleInfoProvider>()

    @OptIn(ExperimentalStdlibApi::class)
    private val knownTypesMap = buildMap<FqName, FqName> { // Map<KnownType, Scope>
        put(FqNames.APPLICATION, FqNames.APPLICATION_SCOPE)

        put(FqNames.VIEWMODEL, FqNames.VIEWMODEL_SCOPE)

        put(FqNames.LISTENABLE_WORKER, FqNames.WORKER_SCOPE)
        put(FqNames.WORKER, FqNames.WORKER_SCOPE)

        put(FqNames.SERVICE, FqNames.SERVICE_SCOPE)
        put(FqNames.INTENT_SERVICE, FqNames.SERVICE_SCOPE)

        put(FqNames.ACTIVITY, FqNames.ACTIVITY_SCOPE)
        put(FqNames.COMPONENT_ACTIVITY, FqNames.ACTIVITY_SCOPE)
        put(FqNames.CORE_COMPONENT_ACTIVITY, FqNames.ACTIVITY_SCOPE)
        put(FqNames.APPCOMPAT_ACTIVITY, FqNames.ACTIVITY_SCOPE)
        put(FqNames.FRAGMENT_ACTIVITY, FqNames.ACTIVITY_SCOPE)

        put(FqNames.FRAGMENT, FqNames.FRAGMENT_SCOPE)
        put(FqNames.DIALOG_FRAGMENT, FqNames.FRAGMENT_SCOPE)

        put(FqNames.VIEW, FqNames.VIEW_SCOPE)
        put(FqNames.VIEW_GROUP, FqNames.VIEW_SCOPE)
    }

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
        val annotationEntry = clas.findAnnotation(FqNames.CONTRIBUTES_ANDROID_BINDING, module) ?: return null
        val scope = annotationEntry.scopeOrNull(module)
        val baseType = clas.superTypeListEntries
            .asSequence()
            .mapNotNull { it.typeReference }
            .filterNot { it.isInterface() }
            .mapNotNull { it.fqNameOrNull(module) }
            .firstOrNull()
        val implicitScope = knownTypesMap[baseType]
        if (scope != null && implicitScope != null && scope != implicitScope) {
            error("Scope mismatch. Implied scope '$implicitScope' does not match supplied scope: '$scope'")
        }
        val resolvedScope = scope ?: implicitScope ?: error(
            "Class '${clas.name}' is annotated with '${FqNames.CONTRIBUTES_ANDROID_BINDING}', but the " +
                    "appropriate scope could not be resolved.\n" +
                    "You can fix this either by directly extending one of the known supertypes, " +
                    "or by explicitly providing a scope parameter in the annotation.\n" +
                    "Known supertypes that may be extended are:\n" +
                    knownTypesMap.keys.joinToString("\n") { "- $it" }
        )

        return dynamicProviderMap.getOrPut(resolvedScope) {
            val scopeDescriptor = resolvedScope.classDescriptorOrNull(module)
            val injectorDefinition = scopeDescriptor?.annotationOrNull(FqNames.DEFINE_INJECTOR_BINDING)
            val instanceDefinition = scopeDescriptor?.annotationOrNull(FqNames.DEFINE_INSTANCE_BINDING)

            when {
                injectorDefinition != null -> AutoInjectorModuleInfoProvider(resolvedScope.asClassName(module))
                instanceDefinition != null -> InstanceModuleInfoProvider(
                    FqNames.CONTRIBUTES_ANDROID_BINDING,
                    resolvedScope.asClassName(module),
                    instanceDefinition.getValueAsClassName("baseType", module)
                )
                else -> error(
                    "Invalid scope '$resolvedScope'. All scope definitions used with " +
                            "${FqNames.CONTRIBUTES_ANDROID_BINDING} must have either " +
                            "'@${FqNames.DEFINE_INJECTOR_BINDING}' or '@${FqNames.DEFINE_INSTANCE_BINDING}' " +
                            "meta annotations"
                )
            }
        }
    }
}
