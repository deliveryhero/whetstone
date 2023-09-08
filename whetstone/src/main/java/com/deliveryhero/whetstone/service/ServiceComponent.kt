package com.deliveryhero.whetstone.service

import android.app.Service
import com.deliveryhero.whetstone.app.ApplicationScope
import com.deliveryhero.whetstone.injector.MembersInjectorMap
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.optional.SingleIn
import dagger.BindsInstance

/**
 * A Dagger component that has the lifetime of the [android.app.Service].
 */
@ContributesSubcomponent(scope = ServiceScope::class, parentScope = ApplicationScope::class)
@SingleIn(ServiceScope::class)
public interface ServiceComponent {
    public val membersInjectorMap: MembersInjectorMap

    /**
     * Interface for creating a [ServiceComponent].
     */
    @ContributesSubcomponent.Factory
    public interface Factory {
        public fun create(@BindsInstance service: Service): ServiceComponent
    }

    @ContributesTo(ApplicationScope::class)
    public interface ParentComponent {
        public fun getServiceComponentFactory(): Factory
    }
}
