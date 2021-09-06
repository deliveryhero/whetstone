package com.deliveryhero.whetstone.viewmodel

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * A [MapKey] annotation for maps with [KClass] of [ViewModel] keys.
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
)
@Retention(value = AnnotationRetention.RUNTIME)
@MapKey
public annotation class ViewModelKey(val value: KClass<out ViewModel>)
