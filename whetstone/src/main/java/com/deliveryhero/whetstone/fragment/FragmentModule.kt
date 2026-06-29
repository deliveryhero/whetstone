package com.deliveryhero.whetstone.fragment

import androidx.fragment.app.Fragment
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Multibinds
import kotlin.reflect.KClass

@BindingContainer
@ContributesTo(FragmentScope::class)
public interface FragmentModule {

    @Multibinds(allowEmpty = true)
    public fun provideFragments(): Map<KClass<*>, Fragment>
}
