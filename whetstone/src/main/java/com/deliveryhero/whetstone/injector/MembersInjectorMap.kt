package com.deliveryhero.whetstone.injector

import dagger.MembersInjector
import kotlin.reflect.KClass

internal typealias MembersInjectorMap = Map<KClass<*>, MembersInjector<*>>
