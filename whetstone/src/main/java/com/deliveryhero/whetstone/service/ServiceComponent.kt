package com.deliveryhero.whetstone.service

import android.app.Service
import com.deliveryhero.whetstone.SingleIn
import com.deliveryhero.whetstone.app.ApplicationScope
import com.deliveryhero.whetstone.injector.MembersInjectorMap
import com.squareup.anvil.annotations.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metro.Provides

/**
 * A Metro component that has the lifetime of the [android.app.Service].
 */
@GraphExtension
public interface ServiceComponent {
    public val membersInjectorMap: MembersInjectorMap

    /**
     * Interface for creating a [ServiceComponent].
     */
    @GraphExtension.Factory
    public interface Factory {
        public fun create(@Provides service: Service): ServiceComponent
    }

    @ContributesTo(ApplicationScope::class)
    public interface ParentComponent {
        public fun getServiceComponentFactory(): Factory
    }
}
