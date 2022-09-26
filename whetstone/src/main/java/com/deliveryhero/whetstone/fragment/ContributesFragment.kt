package com.deliveryhero.whetstone.fragment

import androidx.fragment.app.Fragment
import com.deliveryhero.whetstone.InternalWhetstoneApi
import com.deliveryhero.whetstone.meta.AutoInstanceBinding

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
 *     @ClassKey(MyFragment::class)
 *     fun binds(target: MyFragment): Fragment
 * }
 * ```
 */
@OptIn(InternalWhetstoneApi::class)
@AutoInstanceBinding(base = Fragment::class, scope = FragmentScope::class)
public annotation class ContributesFragment
