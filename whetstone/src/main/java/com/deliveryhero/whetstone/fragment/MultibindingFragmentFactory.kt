package com.deliveryhero.whetstone.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.deliveryhero.whetstone.component.FragmentComponent
import com.deliveryhero.whetstone.scope.ActivityScope
import com.squareup.anvil.annotations.ContributesBinding
import javax.inject.Inject
import javax.inject.Provider

/**
 * A [FragmentFactory] that can hold onto multiple other FragmentFactory [Provider]'s.
 */
@ContributesBinding(ActivityScope::class)
public class MultibindingFragmentFactory @Inject constructor(
    private val fragmentComponentFactory: FragmentComponent.Factory
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        val fragmentComponent = fragmentComponentFactory.create()
        val fragmentClass = loadFragmentClass(classLoader, className)
        val fragmentMap = fragmentComponent.getFragmentMap()
        val fragmentProvider = fragmentMap[fragmentClass]
        return try {
            fragmentProvider?.get() ?: super.instantiate(classLoader, className)
        } catch (throwable: Throwable) {
            throw if (fragmentProvider == null)
                IllegalStateException(
                    "${fragmentClass.name} could not be instantiated. Did you forget to contribute it? Ensure the " +
                            "fragment class is annotated with '${ContributesFragment::class.java.name}' " +
                            "and has an '@Inject constructor'",
                    throwable
                )
            else throwable
        }
    }
}
