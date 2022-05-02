package com.deliveryhero.whetstone.component

import android.app.Service
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.injector.MembersInjectorMap
import com.deliveryhero.whetstone.scope.ApplicationScope
import com.deliveryhero.whetstone.scope.ServiceScope
import com.squareup.anvil.annotations.ContributesSubcomponent
import com.squareup.anvil.annotations.ContributesTo
import dagger.BindsInstance
import dagger.Module
import dagger.multibindings.Multibinds

/**
 * A Dagger component that has the lifetime of the [android.app.Service].
 */
@ContributesSubcomponent(scope = ServiceScope::class, parentScope = ApplicationScope::class)
@SingleIn(ServiceScope::class)
public interface ServiceComponent {
    public fun getMembersInjectorMap(): MembersInjectorMap

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

@Module
@ContributesTo(ServiceScope::class)
public interface ServiceModule {

    @Multibinds
    public fun membersInjectors(): MembersInjectorMap
}
