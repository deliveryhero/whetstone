package com.deliveryhero.whetstone

import kotlin.reflect.KClass

public annotation class DefineInstanceBinding(val baseType: KClass<*>)

public annotation class DefineInjectorBinding
