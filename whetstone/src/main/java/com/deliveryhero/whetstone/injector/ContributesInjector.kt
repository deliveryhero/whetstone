package com.deliveryhero.whetstone.injector

import dagger.MembersInjector
import kotlin.reflect.KClass

/**
 * Marker annotation signalling that the compiler should generate necessary [MembersInjector]
 * bindings for the annotated class.
 *
 * For example:
 * Given this annotated class
 * ```
 * @ContributesInjector(ActivityScope::class)
 * class MyActivity : FragmentActivity() {
 *     @Inject lateinit var someEntity: SomeEntity
 * }
 * ```
 * a complementary module will be generated
 * ```
 * @Module
 * @ContributesTo(ActivityScope::class)
 * interface MyActivityModule {
 *     @Binds
 *     @IntoMap
 *     @ClassKey(MyActivity::class)
 *     fun binds(target: MembersInjector<MyActivity>): MembersInjector<*>
 * }
 * ```
 */
@Deprecated("Legacy API. Custom integrations are encouraged to use the ContributesInjectorMeta annotation instead")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
public annotation class ContributesInjector(val scope: KClass<*>)
