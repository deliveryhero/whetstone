package com.deliveryhero.injection.injector

import kotlin.reflect.KClass

/**
 * Marker annotation signalling that the compiler should generate necessary [AnvilInjector]
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
 * an injector class will be generated
 * ```
 * class MyActivityInjector @Inject constructor(
 *     override val membersInjector: MembersInjector<MyActivity>
 * ) : AnvilInjector<MyActivity>
 * ```
 * as well as a complementary module
 * ```
 * @Module
 * @ContributesTo(ActivityScope::class)
 * interface MyActivityModule {
 *     @Binds
 *     @IntoMap
 *     @ClassKey(MyActivity::class)
 *     fun binds(target: MyActivityInjector): AnvilInjector<*>
 * }
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
public annotation class ContributesInjector(val scope: KClass<*>)
