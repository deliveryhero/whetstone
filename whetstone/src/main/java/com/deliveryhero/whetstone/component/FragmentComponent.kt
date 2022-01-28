package com.deliveryhero.whetstone.component

import androidx.fragment.app.Fragment
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.scope.ActivityScope
import com.deliveryhero.whetstone.scope.FragmentScope
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import javax.inject.Provider

/**
 * A Dagger component that has the lifetime of the [androidx.fragment.app.Fragment].
 */
@ContributesSubcomponent(scope = FragmentScope::class, parentScope = ActivityScope::class)
@SingleIn(FragmentScope::class)
public interface FragmentComponent {
    public fun getFragmentMap(): Map<Class<*>, Provider<Fragment>>

    /**
     * Interface for creating an [FragmentComponent].
     */
    @ContributesSubcomponent.Factory
    public interface Factory {
        public fun create(): FragmentComponent
    }

    @ContributesTo(ActivityScope::class)
    public interface ParentComponent {
        public fun getFragmentComponentFactory(): Factory
    }
}
