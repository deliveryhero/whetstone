package com.deliveryhero.injection.lifecycle

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.deliveryhero.injection.ForScope
import com.deliveryhero.injection.scope.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module
@ContributesTo(ApplicationScope::class)
public object ApplicationCoroutineScopeModule {

    @Provides
    @ForScope(ApplicationScope::class)
    public fun provideCoroutineScope(@ForScope(ApplicationScope::class) lifecycleOwner: LifecycleOwner): CoroutineScope {
        return lifecycleOwner.lifecycleScope
    }
}
