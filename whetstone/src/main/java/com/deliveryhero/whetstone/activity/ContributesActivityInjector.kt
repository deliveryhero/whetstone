package com.deliveryhero.whetstone.activity

import com.deliveryhero.whetstone.InternalWhetstoneApi
import com.deliveryhero.whetstone.meta.AutoInjectorBinding

/**
 * Marker annotation signalling that the compiler should generate necessary members injector
 * bindings for the annotated activity.
 *
 * For example:
 * Given this annotated activity
 * ```
 * @ContributesActivityInjector
 * class MyActivity : Activity()
 * ```
 * a complementary module will be generated
 * ```
 * @Module
 * @ContributesTo(ActivityScope::class)
 * interface MyActivityModule {
 *     @Binds
 *     @IntoMap
 *     @LazyClassKey(MyActivity::class)
 *     fun binds(target: MembersInjector<MyActivity>): MembersInjector<*>
 * }
 * ```
 */
@OptIn(InternalWhetstoneApi::class)
@AutoInjectorBinding(scope = ActivityScope::class)
public annotation class ContributesActivityInjector
