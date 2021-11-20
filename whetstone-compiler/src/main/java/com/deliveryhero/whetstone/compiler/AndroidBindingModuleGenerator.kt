package com.deliveryhero.whetstone.compiler

import com.google.auto.service.AutoService
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.CodeGenerator
import com.squareup.anvil.compiler.api.GeneratedFile
import com.squareup.anvil.compiler.api.createGeneratedFile
import com.squareup.anvil.compiler.internal.*
import com.squareup.kotlinpoet.ClassName
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

@AutoService(CodeGenerator::class)
public class AndroidBindingModuleGenerator : CodeGenerator {

    private val bindingAnnotationFn = FqName("com.deliveryhero.whetstone.ContributesAndroidBinding")

    @OptIn(ExperimentalStdlibApi::class)
    private val autoBindingMap = buildMap<FqName, ModuleInfoProvider> {
        val fragmentProvider = InstanceModuleInfoProvider(
            bindingAnnotationFn,
            ClassName.bestGuess("com.deliveryhero.whetstone.scope.FragmentScope"),
            ClassName.bestGuess("androidx.fragment.app.Fragment")
        )
        put(FqName("androidx.fragment.app.Fragment"), fragmentProvider)
        put(FqName("androidx.fragment.app.DialogFragment"), fragmentProvider)

        val viewModelProvider = InstanceModuleInfoProvider(
            bindingAnnotationFn,
            ClassName.bestGuess("com.deliveryhero.whetstone.scope.ViewModelScope"),
            ClassName.bestGuess("androidx.lifecycle.ViewModel")
        )
        put(FqName("androidx.lifecycle.ViewModel"), viewModelProvider)

        val workerProvider = InstanceModuleInfoProvider(
            bindingAnnotationFn,
            ClassName.bestGuess("com.deliveryhero.whetstone.worker.WorkerScope"),
            ClassName.bestGuess("androidx.work.ListenableWorker")
        )
        put(FqName("androidx.work.ListenableWorker"), workerProvider)
        put(FqName("androidx.work.Worker"), workerProvider)

        val activityProvider = AutoInjectorModuleInfoProvider(
            ClassName.bestGuess("com.deliveryhero.whetstone.scope.ActivityScope")
        )
        put(FqName("android.app.Activity"), activityProvider)
        put(FqName("androidx.activity.ComponentActivity"), activityProvider)
        put(FqName("androidx.core.app.ComponentActivity"), activityProvider)
        put(FqName("androidx.appcompat.app.AppCompatActivity"), activityProvider)

        val serviceProvider = AutoInjectorModuleInfoProvider(
            ClassName.bestGuess("com.deliveryhero.whetstone.scope.ServiceScope")
        )
        put(FqName("android.app.Service"), serviceProvider)
        put(FqName("android.app.IntentService"), serviceProvider)

        val viewProvider = AutoInjectorModuleInfoProvider(
            ClassName.bestGuess("com.deliveryhero.whetstone.scope.ViewScope")
        )
        put(FqName("android.view.View"), viewProvider)
        put(FqName("android.view.ViewGroup"), viewProvider)

        val applicationProvider = AutoInjectorModuleInfoProvider(
            ClassName.bestGuess("com.deliveryhero.whetstone.scope.ApplicationScope")
        )
        put(FqName("android.app.Application"), applicationProvider)
    }

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
        val annotationEntry = clas.findAnnotation(bindingAnnotationFn, module)
            ?: return null
        val boundType = annotationEntry.findAnnotationArgument<PsiElement>("boundType", 0)
            ?.fqNameOrNull(module)
            ?: clas.superTypeListEntries
                .asSequence()
                .mapNotNull { it.typeReference }
                .filterNot { it.isInterface() }
                .mapNotNull { it.fqNameOrNull(module) }
                .firstOrNull()
            ?: Unit::class.fqName

        return autoBindingMap[boundType] ?: error(
            "Class '${clas.name}' is annotated with 'ContributesAndroidBinding', but the appropriate scope " +
                    "could not be resolved.\n" +
                    "You can fix this either by directly extending one of the known supertypes, " +
                    "or by explicitly providing a boundType parameter in the annotation.\n" +
                    "Known supertypes are:\n" +
                    autoBindingMap.keys.joinToString("\n") { "- $it" } + "\n\n" +
                    "Found: $boundType"
        )
    }
}
