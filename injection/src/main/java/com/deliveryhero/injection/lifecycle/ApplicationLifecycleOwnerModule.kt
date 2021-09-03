package com.deliveryhero.injection.lifecycle

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.deliveryhero.injection.ForScope
import com.deliveryhero.injection.scope.ApplicationScope
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
