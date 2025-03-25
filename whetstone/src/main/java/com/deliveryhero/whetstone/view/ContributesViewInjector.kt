package com.deliveryhero.whetstone.view

import com.deliveryhero.whetstone.InternalWhetstoneApi
import com.deliveryhero.whetstone.meta.AutoInjectorBinding

/**
 * Marker annotation signalling that the compiler should generate necessary members injector
 * bindings for the annotated view.
 *
 * For example:
 * Given this annotated view
 * ```
 * @ContributesViewInjector
 * class MyView : View()
 * ```
 * a complementary module will be generated
 * ```
 * @Module
 * @ContributesTo(ViewScope::class)
 * interface MyViewModule {
 *     @Binds
 *     @IntoMap
 *     @LazyClassKey(MyView::class)
 *     fun binds(target: MembersInjector<MyView>): MembersInjector<*>
 * }
 * ```
 */
@OptIn(InternalWhetstoneApi::class)
@AutoInjectorBinding(scope = ViewScope::class)
public annotation class ContributesViewInjector
