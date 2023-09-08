package com.deliveryhero.whetstone.app

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.deliveryhero.whetstone.injector.MembersInjectorMap
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.optional.SingleIn
import dagger.BindsInstance
import javax.inject.Singleton

/**
 * A Dagger component that has the lifetime of the [android.app.Application].
 */
@ContributesTo(ApplicationScope::class)
@SingleIn(ApplicationScope::class)
@Singleton
public interface ApplicationComponent {
    public val viewModelFactory: ViewModelProvider.Factory
    public val membersInjectorMap: MembersInjectorMap

    /**
     * Interface for creating an [ApplicationComponent].
     */
    public interface Factory {
        public fun create(@BindsInstance application: Application): ApplicationComponent
    }
}
