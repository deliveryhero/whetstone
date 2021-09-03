package com.deliveryhero.injection

import dagger.Binds
import dagger.Provides
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Scope
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.reflect.KClass

/**
 * Qualifies this provided type (via [Provides], [Binds], [Inject], etc)
 * for a given [value] scope to distinguish it from instances of the same
 * type in other scopes.
 *
 * Note that the [value] does not actually need to be a [Scope]-annotated
 * annotation class. It is _solely_ a key.
 */
@Qualifier
@Retention(RUNTIME)
public annotation class ForScope(val value: KClass<*>)
