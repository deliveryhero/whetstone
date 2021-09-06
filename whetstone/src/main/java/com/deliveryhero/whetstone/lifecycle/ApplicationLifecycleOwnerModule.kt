package com.deliveryhero.whetstone.lifecycle

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.deliveryhero.whetstone.ForScope
import com.deliveryhero.whetstone.scope.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides

@Module
@ContributesTo(ApplicationScope::class)
public object ApplicationLifecycleOwnerModule {

    @Provides
    @ForScope(ApplicationScope::class)
    public fun provideLifecycleOwner(): LifecycleOwner {
        return ProcessLifecycleOwner.get()
    }
}
