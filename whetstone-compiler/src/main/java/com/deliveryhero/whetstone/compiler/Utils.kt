package com.deliveryhero.whetstone.compiler

import com.squareup.anvil.compiler.internal.asClassName
import com.squareup.anvil.compiler.internal.findAnnotationArgument
import com.squareup.anvil.compiler.internal.getAnnotationValue
import com.squareup.anvil.compiler.internal.requireFqName
import com.squareup.kotlinpoet.ClassName
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.resolve.constants.KClassValue

internal fun KtAnnotationEntry.scope(module: ModuleDescriptor): FqName {
    return requireNotNull(scopeOrNull(module))
}

internal fun KtAnnotationEntry.scopeOrNull(module: ModuleDescriptor): FqName? {
    return findAnnotationArgument<PsiElement>("scope", 0)?.requireFqName(module)
}

internal fun AnnotationDescriptor.getValueAsClassName(key: String, module: ModuleDescriptor): ClassName {
    val classValue = getAnnotationValue(key)?.value as KClassValue.Value.NormalClass
    return classValue.classId.asSingleFqName().asClassName(module)
}
