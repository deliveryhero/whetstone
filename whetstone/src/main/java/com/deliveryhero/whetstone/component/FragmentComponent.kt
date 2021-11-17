package com.deliveryhero.whetstone.component

import com.deliveryhero.whetstone.SingleIn
import androidx.fragment.app.Fragment
import javax.inject.Provider
import com.deliveryhero.whetstone.scope.ActivityScope
import com.deliveryhero.whetstone.scope.FragmentScope
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo

/**
 * A Dagger component that has the lifetime of the [androidx.fragment.app.Fragment].
 */
@ContributesSubcomponent(
    scope = FragmentScope::class,
    parentScope = ActivityScope::class
)
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
        public fun createFragmentFactory(): Factory
    }
}
