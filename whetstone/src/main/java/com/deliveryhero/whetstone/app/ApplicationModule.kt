package com.deliveryhero.whetstone.app

import android.app.Application
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.deliveryhero.whetstone.ForScope
import com.deliveryhero.whetstone.injector.MembersInjectorMap
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.Multibinds
import kotlinx.coroutines.CoroutineScope

@Module
@ContributesTo(ApplicationScope::class)
public interface ApplicationModule {

    @Binds
    @ForScope(ApplicationScope::class)
    public fun bindContext(application: Application): Context

    @Multibinds
    public fun membersInjectors(): MembersInjectorMap

    public companion object {

        @Provides
        @ForScope(ApplicationScope::class)
        public fun provideLifecycleOwner(): LifecycleOwner {
            return ProcessLifecycleOwner.get()
        }

        @Provides
        @ForScope(ApplicationScope::class)
        public fun provideCoroutineScope(
            @ForScope(ApplicationScope::class) lifecycleOwner: LifecycleOwner
        ): CoroutineScope {
            return lifecycleOwner.lifecycleScope
        }
    }
}
