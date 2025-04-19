package com.deliveryhero.whetstone.compiler

import com.google.auto.service.AutoService
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.CodeGenerator
import com.squareup.anvil.compiler.api.GeneratedFileWithSources
import com.squareup.anvil.compiler.api.createGeneratedFile
import com.squareup.anvil.compiler.internal.reference.classAndInnerClassReferences
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import kotlin.LazyThreadSafetyMode.NONE

@AutoService(CodeGenerator::class)
public class WhetstoneCodeGenerator : CodeGenerator {

    private var isAnvilGeneratingDaggerFactories = false
    private val codegenHandlers by lazy(NONE) { defaultCodegenHandlers(isAnvilGeneratingDaggerFactories) }

    override fun isApplicable(context: AnvilContext): Boolean {
        isAnvilGeneratingDaggerFactories = context.generateFactories
        return true
    }

    override fun generateCode(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>
    ): Collection<GeneratedFileWithSources> {
        return projectFiles
            .classAndInnerClassReferences(module)
            .flatMap { clas -> codegenHandlers.mapNotNull { it.processClass(clas, module) } }
            .map { info ->
                createGeneratedFile(
                    codeGenDir = codeGenDir,
                    packageName = info.packageName,
                    fileName = info.fileName,
                    content = info.content,
                    sourceFiles = setOf(info.sourceFile),
                )
            }.toList()
    }
}
