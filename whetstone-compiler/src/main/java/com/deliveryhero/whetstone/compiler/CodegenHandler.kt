package com.deliveryhero.whetstone.compiler

import com.google.devtools.ksp.symbol.KSClassDeclaration

internal interface CodegenHandler {

    fun processClass(clas: KSClassDeclaration): Collection<GeneratedFileInfo>
}
