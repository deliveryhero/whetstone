package com.deliveryhero.whetstone.compiler

import com.deliveryhero.whetstone.compiler.handlers.AppComponentHandler
import com.deliveryhero.whetstone.compiler.handlers.BindingsModuleHandler
import com.google.auto.service.AutoService
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.CodeGenerator
import com.squareup.anvil.compiler.api.GeneratedFileWithSources
import com.squareup.anvil.compiler.api.createGeneratedFile
import com.squareup.anvil.compiler.internal.reference.classAndInnerClassReferences
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

@AutoService(CodeGenerator::class)
public class WhetstoneCodeGenerator : CodeGenerator {

    private val codegenHandlers = mutableListOf<CodegenHandler>()

    override fun isApplicable(context: AnvilContext): Boolean {
        require(codegenHandlers.isEmpty())
        codegenHandlers += BindingsModuleHandler(context.generateFactories)
        codegenHandlers += AppComponentHandler()
        return true
    }

    override fun generateCode(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>
    ): Collection<GeneratedFileWithSources> {
        return projectFiles
            .classAndInnerClassReferences(module)
            .flatMap { clas -> codegenHandlers.flatMap { it.processClass(clas, module) } }
            .mapNotNull { info ->
                when (info.fileType) {
                    GeneratedFileType.KOTLIN -> {
                        createGeneratedFile(
                            codeGenDir = codeGenDir,
                            packageName = info.packageName,
                            fileName = info.fileName,
                            content = info.content,
                            sourceFiles = setOfNotNull(info.sourceFile),
                        )
                    }

                    GeneratedFileType.PROGUARD -> {
                        writeProguardFileContent(
                            codeGenDir = codeGenDir,
                            packageName = info.packageName,
                            fileName = info.fileName,
                            content = info.content,
                        )
                        null
                    }
                }
            }
            .toSet()
    }
}


private fun writeProguardFileContent(
    codeGenDir: File,
    packageName: String,
    fileName: String,
    content: String,
): File {
    val directory = File(codeGenDir, packageName)
    val file = File(directory, "$fileName.txt")
    check(file.parentFile.exists() || file.parentFile.mkdirs()) {
        "Could not generate package directory: ${file.parentFile}"
    }
    file.writeText(content)
    return file
}
