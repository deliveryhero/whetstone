package com.deliveryhero.whetstone.compiler

import com.squareup.anvil.compiler.internal.reference.AnnotationReference
import com.squareup.anvil.compiler.internal.reference.argumentAt

internal fun <T : Any> AnnotationReference.getValue(name: String, index: Int): T {
    return argumentAt(name, index)!!.value()
}
