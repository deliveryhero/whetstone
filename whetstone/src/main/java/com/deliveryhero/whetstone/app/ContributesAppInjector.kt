package com.deliveryhero.whetstone.app

import com.deliveryhero.whetstone.InternalWhetstoneApi
import com.deliveryhero.whetstone.meta.AutoInjectorBinding

/**
 * Marker annotation signalling that the compiler should generate necessary members injector
 * bindings for the annotated application.
 *
 * For example:
 * Given this annotated application
 * ```
 * @ContributesAppInjector
 * class MyApplication : Application()
 * ```
 * a complementary module will be generated
 * ```
 * @Module
 * @ContributesTo(ApplicationScope::class)
 * interface MyApplicationModule {
 *     @Binds
 *     @IntoMap
 *     @ClassKey(MyApplication::class)
 *     fun binds(target: MembersInjector<MyApplication>): MembersInjector<*>
 * }
 * ```
 */
@OptIn(InternalWhetstoneApi::class)
@AutoInjectorBinding(ApplicationScope::class)
public annotation class ContributesAppInjector
