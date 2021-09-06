package com.deliveryhero.whetstone.fragment

/**
 * Marker annotation signalling that the compiler should generate necessary instance
 * bindings for the annotated fragment.
 *
 * For example:
 * Given this annotated fragment
 * ```
 * @ContributesFragment
 * class MyFragment @Inject constructor() : Fragment()
 * ```
 * a complementary module will be generated
 * ```
 * @Module
 * @ContributesTo(FragmentScope::class)
 * interface MyFragmentModule {
 *     @Binds
 *     @IntoMap
 *     @FragmentKey(MyFragment::class)
 *     fun binds(target: MyFragment): Fragment
 * }
 * ```
 */
public annotation class ContributesFragment
