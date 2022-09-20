package com.deliveryhero.whetstone.service

import com.deliveryhero.whetstone.InternalWhetstoneApi
import com.deliveryhero.whetstone.meta.AutoInjectorBinding

/**
 * Marker annotation signalling that the compiler should generate necessary members injector
 * bindings for the annotated service.
 *
 * For example:
 * Given this annotated service
 * ```
 * @ContributesService
 * class MyService : Service()
 * ```
 * a complementary module will be generated
 * ```
 * @Module
 * @ContributesTo(ServiceScope::class)
 * interface MyServiceModule {
 *     @Binds
 *     @IntoMap
 *     @ClassKey(MyService::class)
 *     fun binds(target: MembersInjector<MyService>): MembersInjector<*>
 * }
 * ```
 */
@OptIn(InternalWhetstoneApi::class)
@AutoInjectorBinding(scope = ServiceScope::class)
public annotation class ContributesService
