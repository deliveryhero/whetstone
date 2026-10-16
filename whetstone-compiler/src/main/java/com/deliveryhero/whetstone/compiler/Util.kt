package com.deliveryhero.whetstone.compiler

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ksp.toClassName

internal fun KSAnnotation.fqName(): String? = annotationType.resolve().declaration.qualifiedName?.asString()

internal fun KSAnnotation.metaAnnotations(): Sequence<KSAnnotation> =
    annotationType.resolve().declaration.annotations

internal fun <T> KSAnnotation.argumentValue(name: String): T {
    @Suppress("UNCHECKED_CAST")
    return arguments.first { it.name?.asString() == name }.value as T
}

internal fun KSAnnotation.classNameArgument(name: String): ClassName {
    return argumentValue<KSType>(name).toClassName()
}
