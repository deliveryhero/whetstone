package com.deliveryhero.injection.fragment

import androidx.fragment.app.Fragment
import com.deliveryhero.injection.scope.FragmentScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.multibindings.Multibinds

@Module
@ContributesTo(FragmentScope::class)
public interface FragmentModule {

    @Multibinds
    public fun bindFragmentMap(): FragmentMap
}
