package com.deliveryhero.whetstone.component

import android.app.Application
import com.deliveryhero.whetstone.injector.MembersInjectorMap
import com.deliveryhero.whetstone.scope.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.BindsInstance

/**
 * A Dagger component that has the lifetime of the [android.app.Application].
 */
@ContributesTo(ApplicationScope::class)
public interface ApplicationComponent {
    public fun getMembersInjectorMap(): MembersInjectorMap
    public fun getActivityComponentFactory(): ActivityComponentFactory
}

/**
 * Interface for creating an [ApplicationComponent].
 */
public interface ApplicationComponentFactory {
    public fun create(@BindsInstance application: Application): ApplicationComponent
}
