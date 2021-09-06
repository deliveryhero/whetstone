package com.deliveryhero.injection.fragment

import androidx.fragment.app.Fragment
import com.deliveryhero.injection.scope.FragmentScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

// Workaround for: https://stackoverflow.com/questions/61558178/provide-a-multibiding-of-an-empty-map-of-providers-with-dagger-2
@Module
@ContributesTo(FragmentScope::class)
public object FragmentModule {

    @Provides
    @IntoMap
    @FragmentKey(NullFragment::class)
    public fun provideNullFragment(): Fragment {
        error("${NullFragment::class.java.name} should never be instantiated.")
    }

    internal class NullFragment : Fragment()
}
