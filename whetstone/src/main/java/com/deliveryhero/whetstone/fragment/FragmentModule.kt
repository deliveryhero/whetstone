package com.deliveryhero.whetstone.fragment

import androidx.fragment.app.Fragment
import com.deliveryhero.whetstone.scope.FragmentScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.multibindings.Multibinds

@Module
@ContributesTo(FragmentScope::class)
public interface FragmentModule {

    @Multibinds
    public fun provideFragments(): Map<Class<*>, Fragment>
}
