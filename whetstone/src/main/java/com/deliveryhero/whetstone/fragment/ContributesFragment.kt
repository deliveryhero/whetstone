package com.deliveryhero.whetstone.fragment

import androidx.fragment.app.Fragment
import com.deliveryhero.whetstone.AutoScopedBinding
import com.deliveryhero.whetstone.InternalInjectApi
import com.deliveryhero.whetstone.scope.FragmentScope

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
@OptIn(InternalInjectApi::class)
@AutoScopedBinding(base = Fragment::class, scope = FragmentScope::class, multibindingKey = FragmentKey::class)
public annotation class ContributesFragment
