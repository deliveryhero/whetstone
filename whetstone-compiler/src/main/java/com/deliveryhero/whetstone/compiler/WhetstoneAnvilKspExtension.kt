package com.deliveryhero.whetstone.compiler

import com.deliveryhero.whetstone.compiler.handlers.AppComponentHandler
import com.deliveryhero.whetstone.compiler.handlers.BindingsModuleHandler
import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.AnvilKspExtension
import java.io.OutputStreamWriter

/**
 * Runs Whetstone's custom code generation (bindings modules for `@ContributesViewModel`,
 * `@ContributesFragment`, `@ContributesInjector`, `@ContributesAppInjector`, etc, plus the
 * app-root `@MergeComponent` generation) as an Anvil-KSP [AnvilKspExtension] instead of the
 * legacy `CodeGenerator` SPI, which Anvil-KSP does not invoke at all - see FORK.md "Custom Code
 * Generators".
 */
internal class WhetstoneAnvilKspExtension(
    private val env: SymbolProcessorEnvironment,
    generateFactories: Boolean,
) : AnvilKspExtension {

    @AutoService(AnvilKspExtension.Provider::class)
    class Provider : AnvilKspExtension.Provider {
        private var generateFactories: Boolean = false

        override fun isApplicable(context: AnvilContext): Boolean {
            generateFactories = context.generateFactories
            return true
        }

        override fun create(environment: SymbolProcessorEnvironment): AnvilKspExtension {
            environment.logger.info("Whetstone: starting codegen extension, generateFactories=$generateFactories")
            return WhetstoneAnvilKspExtension(environment, generateFactories)
        }
    }

    private val codegenHandlers = listOf(
        BindingsModuleHandler(generateFactories),
        AppComponentHandler(),
    )

    override val supportedAnnotationTypes: Set<String> = setOf(
        FqNames.CONTRIBUTES_APP,
        FqNames.CONTRIBUTES_INJECTOR,
        FqNames.AUTO_INJECTOR,
        FqNames.AUTO_INSTANCE,
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val classes = resolver.getNewFiles()
            .flatMap { it.declarations }
            .flatMap { it.classDeclarations() }
            .toList()

        env.logger.info("Whetstone: scanning ${classes.size} new class(es) for codegen triggers")

        var generatedCount = 0
        for (clas in classes) {
            for (handler in codegenHandlers) {
                val generatedFiles = handler.processClass(clas)
                for (info in generatedFiles) {
                    write(info)
                    generatedCount++
                    env.logger.info(
                        "Whetstone: generated ${info.packageName}.${info.fileName} " +
                            "(${info.fileType}) from ${clas.qualifiedName?.asString()}"
                    )
                }
            }
        }

        if (generatedCount > 0) {
            env.logger.info("Whetstone: generated $generatedCount file(s) this round")
        }

        return emptyList()
    }

    private fun write(info: GeneratedFileInfo) {
        val dependencies = Dependencies(aggregating = false, *info.sourceFiles.toTypedArray())
        val stream = when (info.fileType) {
            GeneratedFileType.KOTLIN ->
                env.codeGenerator.createNewFile(dependencies, info.packageName, info.fileName, "kt")

            GeneratedFileType.PROGUARD ->
                env.codeGenerator.createNewFileByPath(dependencies, "${info.packageName}/${info.fileName}", "pro")
        }
        OutputStreamWriter(stream).use { it.write(info.content) }
    }

    private fun KSDeclaration.classDeclarations(): List<KSClassDeclaration> {
        if (this !is KSClassDeclaration) return emptyList()
        return listOf(this) + declarations.flatMap { it.classDeclarations() }
    }
}
