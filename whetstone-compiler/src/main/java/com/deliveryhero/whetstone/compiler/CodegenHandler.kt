package com.deliveryhero.whetstone.compiler

import com.squareup.anvil.compiler.internal.reference.ClassReference
import org.jetbrains.kotlin.descriptors.ModuleDescriptor

internal interface CodegenHandler {

    fun processClass(clas: ClassReference, module: ModuleDescriptor): Collection<GeneratedFileInfo>
}
