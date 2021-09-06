package com.deliveryhero.whetstone.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.deliveryhero.whetstone.InternalInjectApi
import com.deliveryhero.whetstone.component.FragmentComponentFactory
import com.deliveryhero.whetstone.scope.ActivityScope
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider

/**
 * A [FragmentFactory] that can hold onto multiple other FragmentFactory [Provider]'s.
 *
 * Note this was designed to be used with [FragmentKey].
 */
@OptIn(InternalInjectApi::class)
@ContributesBinding(ActivityScope::class)
public class MultibindingFragmentFactory @Inject constructor(
    private val fragmentComponentFactory: FragmentComponentFactory
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val fragmentComponent = fragmentComponentFactory.create()
        val fragmentClass = loadFragmentClass(classLoader, className)
        val fragmentMap = fragmentComponent.getFragmentMap()

        return try {
            fragmentMap[fragmentClass]?.get() ?: super.instantiate(classLoader, className)
        } catch (e: Throwable) {
            error("Fragment '${fragmentClass.name}' cannot be instantiated. Did you miss to contribute it? Ensure the Fragment class is annotated with '${ContributesFragment::class.java.name}' and has a constructor annotated with '${Inject::class.java.name}'.")
        }
    }
}
