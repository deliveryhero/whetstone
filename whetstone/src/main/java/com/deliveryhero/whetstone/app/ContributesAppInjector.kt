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
 *
 * This annotation can also be used to generate the root app component. To turn on this feature,
 * simply set [generateAppComponent] to `true`. Doing so will generate the following:
 *
 * ```
 * @MergeComponent(ApplicationScope::class)
 * @SingleIn(ApplicationScope::class)
 * @Singleton
 * public interface GeneratedApplicationComponent : ApplicationComponent {
 *    @Component.Factory
 *    public interface Factory : ApplicationComponent.Factory
 *
 *    public companion object Default : Factory by DaggerGeneratedApplicationComponent.factory()
 * }
 * ```
 *
 * This can be very handy for quickly bootstrapping the DI setup. It is ideal for cases where the
 * application component can be built without any external instance dependency (via `@BindsInstance`)
 *
 * **Note**: Generating app component is disabled by default.
 */
@OptIn(InternalWhetstoneApi::class)
@AutoInjectorBinding(ApplicationScope::class)
public annotation class ContributesAppInjector(val generateAppComponent: Boolean = false)
