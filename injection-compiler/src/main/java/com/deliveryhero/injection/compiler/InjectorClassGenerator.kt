package com.deliveryhero.injection.compiler

import com.google.auto.service.AutoService
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.CodeGenerator
import com.squareup.anvil.compiler.api.GeneratedFile
import com.squareup.anvil.compiler.api.createGeneratedFile
import com.squareup.anvil.compiler.internal.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import dagger.MembersInjector
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import java.io.File
import javax.inject.Inject

@AutoService(CodeGenerator::class)
public class InjectorClassGenerator : CodeGenerator {
    private val contributesInjectorFn = FqName("com.deliveryhero.injection.injector.ContributesInjector")
    private val membersInjectorCn = MembersInjector::class.asClassName()
    private val anvilInjectorCn = ClassName("com.deliveryhero.injection.injector", "AnvilInjector")

    override fun isApplicable(context: AnvilContext): Boolean = true

    override fun generateCode(
        codeGenDir: File,
        module: ModuleDescriptor,
        projectFiles: Collection<KtFile>
    ): Collection<GeneratedFile> {

        return projectFiles
            .classesAndInnerClass(module)
            .filter { it.hasAnnotation(contributesInjectorFn, module) }
            .map { clas ->
                val info = generateInjectorClass(clas)
                createGeneratedFile(codeGenDir, info.packageName, info.fileName, info.content)
            }.toList()
    }

    private fun generateInjectorClass(annotatedClass: KtClassOrObject): GeneratedFileInfo {
        val annotatedClassName = annotatedClass.asClassName()
        val packageName = annotatedClass.containingKtFile.packageFqName.safePackageString(
            dotPrefix = false,
            dotSuffix = false,
        )
        val outputFileName = annotatedClassName.simpleName + "Injector"
        val propertyName = "membersInjector"
        val propertyType = membersInjectorCn.parameterizedBy(annotatedClassName)

        val constructorSpec = FunSpec.constructorBuilder()
            .addAnnotation(Inject::class)
            .addParameter(propertyName, propertyType)
            .build()
        val membersInjectorSpec = PropertySpec.builder(propertyName, propertyType, KModifier.OVERRIDE)
            .initializer(propertyName)
            .build()

        val content = FileSpec.buildFile(packageName, outputFileName) {
            val classSpec = TypeSpec.classBuilder(outputFileName)
                .addSuperinterface(anvilInjectorCn.parameterizedBy(annotatedClassName))
                .primaryConstructor(constructorSpec)
                .addProperty(membersInjectorSpec)
                .build()
            addType(classSpec)
        }

        return GeneratedFileInfo(packageName, outputFileName, content)
    }
}
