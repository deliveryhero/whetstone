package com.deliveryhero.whetstone.compiler

import com.deliveryhero.whetstone.compiler.handlers.AppComponentHandler
import com.deliveryhero.whetstone.compiler.handlers.BindingsModuleHandler
import com.squareup.anvil.compiler.internal.reference.ClassReference
import org.jetbrains.kotlin.descriptors.ModuleDescriptor

internal interface CodegenHandler {

    fun processClass(clas: ClassReference, module: ModuleDescriptor): GeneratedFileInfo?
}

internal fun defaultCodegenHandlers(generateFactories: Boolean): List<CodegenHandler> {
    return listOf(BindingsModuleHandler(generateFactories), AppComponentHandler())
}
