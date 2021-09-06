package com.deliveryhero.whetstone.fragment

import androidx.fragment.app.Fragment
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * A [MapKey] annotation for maps with [KClass] of [Fragment] keys.
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
)
@Retention(value = AnnotationRetention.RUNTIME)
@MapKey
public annotation class FragmentKey(val value: KClass<out Fragment>)
