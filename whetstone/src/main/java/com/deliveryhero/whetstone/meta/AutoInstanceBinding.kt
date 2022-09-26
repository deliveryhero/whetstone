package com.deliveryhero.whetstone.meta

import com.deliveryhero.whetstone.InternalWhetstoneApi
import kotlin.reflect.KClass

@InternalWhetstoneApi
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
public annotation class AutoInstanceBinding(val base: KClass<*>, val scope: KClass<*>)
