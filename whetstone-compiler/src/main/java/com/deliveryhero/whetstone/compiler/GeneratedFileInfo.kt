package com.deliveryhero.whetstone.compiler

import java.io.File

internal class GeneratedFileInfo(
    val packageName: String,
    val fileName: String,
    val content: String,
    val sourceFile: File?,
    val fileType: GeneratedFileType,
)

internal enum class GeneratedFileType {

    KOTLIN,
    PROGUARD
}
