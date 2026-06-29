package com.deliveryhero.whetstone.fragment

import androidx.fragment.app.Fragment
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.activity.ActivityScope
import com.squareup.anvil.annotations.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Multibinds
import javax.inject.Provider
import kotlin.reflect.KClass

/**
 * A Metro component that has the lifetime of the [androidx.fragment.app.Fragment].
 */
@GraphExtension
public interface FragmentComponent {
    public val fragmentMap: Map<KClass<*>, Provider<Fragment>>

    @Multibinds(allowEmpty = true)
    public fun provideFragments(): Map<KClass<*>, Fragment>

    /**
     * Interface for creating an [FragmentComponent].
     */
    @GraphExtension.Factory
    public interface Factory {
        public fun create(): FragmentComponent
    }

    @ContributesTo(ActivityScope::class)
    public interface ParentComponent {
        public fun getFragmentComponentFactory(): Factory
    }
}
