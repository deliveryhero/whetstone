package com.deliveryhero.whetstone.compiler

import com.google.devtools.ksp.symbol.KSFile

internal class GeneratedFileInfo(
    val packageName: String,
    val fileName: String,
    val content: String,
    val sourceFiles: Set<KSFile>,
    val fileType: GeneratedFileType,
)

internal enum class GeneratedFileType {

    KOTLIN,
    PROGUARD
}
