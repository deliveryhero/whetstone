package com.deliveryhero.whetstone

import kotlin.reflect.KClass

@InternalWhetstoneApi
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
public annotation class AutoScopedBinding(val base: KClass<*>, val scope: KClass<*>)
